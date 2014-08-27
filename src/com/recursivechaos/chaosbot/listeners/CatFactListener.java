package com.recursivechaos.chaosbot.listeners;

/**
 * CatFactListener returns a random catfact on the !catfact command. Reads from
 * the catfacts.txt file in the root of the project. Could be easily added to
 * add any sort of 'random facts/sayings' to the bot.
 * 
 * @author Andrew Bell www.recursivechaos.com
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatFactListener extends ListenerAdapter<PircBotX> {
	Logger logger = LoggerFactory.getLogger(CatFactListener.class);

	@Override
	public void onMessage(final MessageEvent<PircBotX> event) {
		if (event.getMessage().startsWith("!catfact")) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(
						"catfacts.txt"));
				List<String> catfacts = new ArrayList<String>();
				String line = null;
				while ((line = br.readLine()) != null) {
					catfacts.add(line);
				}
				Random randomGenerator = new Random();
				String catfact = catfacts.get(randomGenerator.nextInt(catfacts
						.size()));
				event.getChannel().getBot().sendIRC()
						.message(event.getChannel().getName(), catfact);
				br.close();
			} catch (Exception e) {
				logger.error("Cat Facts not found: " + e.getMessage());

			}
		}
	}
}
