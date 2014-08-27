package com.recursivechaos.chaosbot.bot.objects;

import java.io.File;
import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.exception.IrcException;

import com.recursivechaos.chaosbot.listeners.CatFactListener;
import com.recursivechaos.chaosbot.listeners.dice.DiceListener;
import com.recursivechaos.chaosbot.listeners.redditpreview.RedditPreviewListener;
import com.recursivechaos.chaosbot.listeners.stoopsnoop.LogListener;

public class BotTools {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Configuration<?> buildConfiguration(BotConfiguration settings) {
		// create bot
		Configuration<?> configuration = new Configuration.Builder()
			.setName(settings.getNick())
			// Set the nick of the bot.
			.setNickservPassword(settings.getPassword())
			.setLogin("chaosbot")
			// login part of hostmask, eg name:login@host
			.setAutoNickChange(true)
			// Automatically change nick
			.setCapEnabled(true)
			// Enable CAP features
			.addCapHandler(
					new TLSCapHandler(new UtilSSLSocketFactory()
							.trustAllCertificates(), true))
			.setServerHostname(settings.getServer())
			.addAutoJoinChannel(settings.getChannel())
			.setNickservPassword(settings.getPassword())
			.addListener(new CatFactListener())
			.addListener(new DiceListener())
			.addListener(new RedditPreviewListener())
			.addListener(new LogListener())
			.buildConfiguration();
	
		return configuration;
	}

	public static BotConfiguration loadBotConfig(String[] args) {
		// load bot configuration
		BotConfiguration botConfig = new BotConfiguration();
		try {
	
			// read from file, convert it to user class
			ObjectMapper mapper = new ObjectMapper();
			botConfig = mapper.readValue(new File("BotConfiguration.json"), BotConfiguration.class);
	
		}catch(Exception e){
			e.printStackTrace();
		}
		return botConfig;
	}

	public static void startBot(Configuration<?> config) {
		PircBotX bot = new PircBotX(config);
		try {
			bot.startBot();
			//MainBotConsole.log.info("Bot" + config.getLogin() + " started successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}
	}

}
