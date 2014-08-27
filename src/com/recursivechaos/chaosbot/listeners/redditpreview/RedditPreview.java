package com.recursivechaos.chaosbot.listeners.redditpreview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Rewrite using Jackon, which is already being used elsewhere
 * 
 * @author andrew
 *
 */

public class RedditPreview {
	public enum Type {POST, SUBREDDIT, COMMENT, CONTEXT, SHORT}
	String 		url;
	String 		preview;
	String 		subreddit;
	String 		postTitle;
	String 		comment;
	JSONObject 	json;
	Type		type;
	int			score;
	boolean		nsfw;
	String		desc;
	int			subs;
	String		surl;
	int			replies;
	String		author;
	
	
	/**
	 * Creates a RedditPreview from the raw message from the event
	 * @param message	from PircBotX event object
	 * @throws PreviewException error parsing JSON
	 */
	public RedditPreview(String message) throws PreviewException {
		setURLfromMessage(message);
		setTypeFromURL();
		setJsonFromURL();
		parseJson();
	}

	/**
	 * Parses the JSON according to what type the object is
	 * @throws PreviewException error reading JSON
	 */
	private void parseJson() throws PreviewException {
		JSONObject data;
		// If we try to read a restricted (or possibly just bad) url, this will throw. It appears
		// only to effect short URLs to NSFW subs right now.
		try{
			data = (JSONObject) json.get("data");
		}catch(Exception e){
			throw new PreviewException("Private/NSFW URL",e);
		}
		switch(type){
		case POST:
			JSONArray children = (JSONArray) data.get("children");
			JSONObject child = (JSONObject) children.get(0);
			JSONObject cdata = (JSONObject) child.get("data");
			this.subreddit  = cdata.getString("subreddit");
			this.nsfw		= cdata.getBoolean("over_18");
			this.postTitle  = cdata.getString("title");
			this.score		= cdata.getInt("score");
			this.surl		= "redd.it/" + cdata.getString("id");
			this.author		= cdata.getString("author");
			break;
		case SUBREDDIT:
			this.subreddit	= data.getString("display_name");
			this.nsfw		= data.getBoolean("over18");
			this.desc		= data.getString("header_title");
			this.subs		= data.getInt("subscribers");
			break;
		case COMMENT:
			JSONArray c_children = (JSONArray) data.get("children");
			JSONObject c_child = (JSONObject) c_children.get(0);
			JSONObject c_cdata = (JSONObject) c_child.get("data");
			this.subreddit  = c_cdata.getString("subreddit");
			this.score		= c_cdata.getInt("ups")-c_cdata.getInt("downs");
			this.author		= c_cdata.getString("author");
			this.nsfw		= c_cdata.getBoolean("over_18");
			this.postTitle  = c_cdata.getString("title"); // also not located here
			break;
		case CONTEXT:
			// I'm not sure how to pull a context comment, so treat it like a comment, which I also
			// don't really know how to handle :/
			JSONArray t_children = (JSONArray) data.get("children");
			JSONObject t_child = (JSONObject) t_children.get(0);
			c_cdata = (JSONObject) t_child.get("data");
			this.subreddit  = c_cdata.getString("subreddit");
			this.score		= c_cdata.getInt("ups")-c_cdata.getInt("downs");
			this.author		= c_cdata.getString("author");
			this.nsfw		= c_cdata.getBoolean("over_18");
			this.postTitle  = c_cdata.getString("title"); // also not located here
			break;
		case SHORT:
			// treated as regular post
			JSONArray s_children = (JSONArray) data.get("children");
			JSONObject s_child = (JSONObject) s_children.get(0);
			cdata = (JSONObject) s_child.get("data");
			this.subreddit  = cdata.getString("subreddit");
			this.nsfw		= cdata.getBoolean("over_18");
			this.postTitle  = cdata.getString("title");
			this.score		= cdata.getInt("score");
			this.author		= cdata.getString("author");
			this.surl		= "redd.it/" + cdata.getString("id");
			break;
		default:
			break;	
		}
	}

	/**
	 * setJsonFromURL will set the json object given a valid url
	 * @throws PreviewException
	 */
	private void setJsonFromURL() throws PreviewException {
		String myUrl = this.url;
		if (!myUrl.contains(".json")) {
			myUrl = myUrl.concat("/.json");
		}
		
		if(!myUrl.startsWith("http")){
			myUrl = "http://" + myUrl;
		}
		InputStream is = null;
		try {
			is = new URL(myUrl).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			// This is only grabbing one element, in order to have comments, I need to grab both.
			JSONObject json = new JSONObject(jsonText.substring(jsonText
					.indexOf("{")));
			
			this.json = json;
		} catch (Exception e) {
			throw new PreviewException("Unable to read json.",e);
		} finally {
				try {
					is.close();
				} catch (IOException e) {
					throw new PreviewException("Unable to close json.",e);
				}
		}
	}
	
	/**
	 * Reads all text from the reader
	 * @param rd reader containing json
	 * @return string of json data
	 * @throws IOException
	 */
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * Determines type of reddit link based on the url
	 */
	private void setTypeFromURL() {
		Type myType = null;
		//Short
		if(url.contains("redd.it")){
			myType = Type.SHORT;
			this.url = getRedirectedURL(this.url);
		}else if(url.contains("?context=")){
			myType = Type.CONTEXT;
			// lops off the context, don't know what to do with it yet.
			url = url.substring(0,url.indexOf("?")-1);
		}else if(count(url,"/")==6){
			myType = Type.COMMENT;
		}else if(count(url,"/")==5){
			myType = Type.POST;
		}else if(count(url,"/")==2){
			myType = Type.SUBREDDIT;
			this.url = url + "/about";
		}
		this.type = myType;
	}

	/**
	 * String helper function to find count of string in a string
	 * @param haystack	to search
	 * @param needle	to find
	 * @return			count of finds
	 */
	private int count(String haystack, String needle) {
		int count = 0;
		for(int i = 0;i<haystack.length();i++){
			String straw = Character.toString(haystack.charAt(i));
			if(straw.equals(needle)){
				count++;
			}
		}
		return count;
	}

	/**
	 * setURLfromMessage pulls the url from the message, and trims as necessary
	 * @param message from PircBotX event
	 */
	private void setURLfromMessage(String message) {
		final String[] validURLs = { "reddit.com/","redd.it/" };
		String url = "";
		message = message.toLowerCase();
		// start location of URL
		int start = -1;
		// search message for URL
		for (String u : validURLs) {
			if (message.contains(u)) {
				// If earlier, or not found
				if ((start > message.indexOf(u)) || (start == -1)) {
					start = message.indexOf(u);
					//logger.debug("Index found at " + start);
				}
			}
		}
		// If a URL is found, trim url
		if (start != -1) {
			String ltrim = message.substring(start);
			int end = ltrim.indexOf(" ");
			if (end != -1) {
				url = ltrim.substring(0, end);
			} else {
				url = ltrim;
			}
		}
		// trim end /
		if(url.endsWith("/")){
			url = url.substring(0,url.length()-1);
		}
		this.url = url;
	}
	
	/**
	 * If the url redirects, will loop until it finds the destination
	 * @param url unresolved url
	 * @return	resolved url
	 */
	private String getRedirectedURL(String url) {
		//logger.info("Predirected url: " + url);
		url = "http://"+url;
		// If redirect, finds end url
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setInstanceFollowRedirects(false);
			while (connection.getResponseCode() / 100 == 3) {
				url = connection.getHeaderField("location");
				//logger.info("Long URL: " + url);
				connection = (HttpURLConnection) new URL(url).openConnection();
				connection.setInstanceFollowRedirects(false);
			}
		} catch (MalformedURLException e) {
			//logger.info("Malformed URL: " + e.getMessage());
			url = null;
		} catch (IOException e) {
			//logger.info("IOException: " + e.getMessage());
			url = null;
		}
		//logger.info("Redirected URL: " + url);
		return url;
	}

	/**
	 * Builds a string preview for the url
	 */
	private void buildPreview() {
		switch(type){
		case POST:
			this.preview = this.author + " posted \"" + this.postTitle
				+ "\" to /r/" + this.subreddit + " (+" + this.score + ").";
			if(getNSFW(this.nsfw)!=null){
				preview = preview +  getNSFW(this.nsfw);
			}
			break;
		case SUBREDDIT:
			
			this.preview = "/r/" + this.subreddit + " \"" + this.desc + "\" " 
					+this.subs + " subsrcibers.";
			if(getNSFW(this.nsfw)!=null){
				this.preview = this.preview + getNSFW(this.nsfw) + " " ;
			}
			break;
		case COMMENT:
			this.preview = "Comment posted in \"" + this.postTitle + "\".";
			if(getNSFW(this.nsfw)!=null){
				this.preview = this.preview + getNSFW(this.nsfw);
			}
			break;
		case CONTEXT:
			this.preview = "Comment posted in \"" + this.postTitle + "\".";
			if(getNSFW(this.nsfw)!=null){
				this.preview = this.preview  + getNSFW(this.nsfw);
			}
			break;
		case SHORT:
			// Still not sure what to do about context, just treat as regular post
			this.preview = this.author + " posted \"" + this.postTitle
				+ "\" to /r/" + this.subreddit
				+ " (+" + this.score + ").";
			if(getNSFW(this.nsfw)!=null){
				this.preview = this.preview + getNSFW(this.nsfw);
			}
			break;
		}	
		
	}

	/**
	 * Returns NSFW text if NSFW
	 * @param nsfw2
	 * @return
	 */
	private String getNSFW(boolean nsfw2) {
		if(nsfw2==true){
			return " [NSWF]";
		}else{
			return null;
		}
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPreview() {
		if(preview==null){
			buildPreview();
		}
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public String getSubreddit() {
		return subreddit;
	}

	public void setSubreddit(String subreddit) {
		this.subreddit = subreddit;
	}

	public String getPostTitle() {
		return postTitle;
	}

	public void setPostTitle(String postTitle) {
		this.postTitle = postTitle;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isNsfw() {
		return nsfw;
	}

	public void setNsfw(boolean nsfw) {
		this.nsfw = nsfw;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getSubs() {
		return subs;
	}

	public void setSubs(int subs) {
		this.subs = subs;
	}

	public String getSurl() {
		return surl;
	}

	public void setSurl(String surl) {
		this.surl = surl;
	}

	public int getReplies() {
		return replies;
	}

	public void setReplies(int replies) {
		this.replies = replies;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
}