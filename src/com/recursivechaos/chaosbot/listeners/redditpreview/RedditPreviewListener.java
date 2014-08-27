package com.recursivechaos.chaosbot.listeners.redditpreview;

/**
 * RedditPreviewListener will check messages for a valid reddit url, and
 * display a preview
 * 
 * @author Andrew Bell www.recursivechaos.com
 */
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedditPreviewListener extends ListenerAdapter<PircBotX> {
	Logger logger = LoggerFactory.getLogger(RedditPreviewListener.class);
	
	public void onMessage(final MessageEvent<PircBotX> event) {
		String message = event.getMessage();
		String user = event.getUser().getNick();
		if(isValid(message)&&isValidUser(user)){
			RedditPreview rurl;
			try {
				rurl = new RedditPreview(message);
				if(rurl!=null){
					event.getBot().sendIRC().message(event.getChannel().getName(),rurl.getPreview());
				}
			} catch (PreviewException e) {
				// error'd
			}
		}
	}

	/**O
	 * Checks against banned user list, current in the string array below. Needs to be updated
	 * to remove hard coding
	 * @param user user to check if not on ban list
	 * @return	if user is not on ban list, true
	 */
	private boolean isValidUser(String user) {
		// This doesn't really translate well to the 'universal' concept of the bot, I need to 
		// implement an 'ignored user' file to read from
		final String[] bannedUsers = { "dtbot","dlbot","dsbot"};
		boolean validUser = true;
		user = user.toLowerCase();
		// search message for URL
		for (String u : bannedUsers) {
			if (user.contains(u)) {
				validUser = false;
				logger.info("baned user found");
			}
		}
		return validUser;
	}

	/**
	 * Checks to see if this is a url that the listener can handle
	 * @param message raw message from event
	 * @return true if contains a valid url
	 */
	private boolean isValid(String message) {
		final String[] validURLs = { "reddit.com/","redd.it/" };
		boolean validURL = false;
		message =message.toLowerCase();
		// search message for URL
		for (String u : validURLs) {
			if (message.contains(u)) {
				validURL = true;
				logger.info("Valid URL Found");
			}
		}
		return validURL;
	}
}
