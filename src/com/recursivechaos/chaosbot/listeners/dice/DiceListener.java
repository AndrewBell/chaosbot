package com.recursivechaos.chaosbot.listeners.dice;

/**
 * DiceListener will roll dice when nick types !roll (dice string). Dice should
 * be formated in a D&D style format, such as 12D10+5. User can add flavor text,
 * such as !roll 2D10 to hit, and the bot will add the "to hit" to the end of 
 * the response.
 * 
 * @author Andrew Bell www.recursivechaos.com
 */
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiceListener extends ListenerAdapter<PircBotX> {
	Logger logger = LoggerFactory.getLogger(DiceListener.class);

	@Override
	public void onMessage(final MessageEvent<PircBotX> event) {
		if (event.getMessage().startsWith("!roll")) {
			RollDAO roller = new RollDAOImpl();
			try {
				// Get roll value
				int roll = roller.rollFromString(event.getMessage());
				// Get "flavor text"
				String flavorText = roller.getFlavorText(event.getMessage());
				if (!flavorText.equals("")) {
					flavorText = " " + flavorText;
				}
				// Respond to the channel
				event.getBot()
						.sendIRC()
						.message(
								event.getChannel().getName().toString(),
								event.getUser().getNick() + " rolled " + roll
										+ flavorText + ".");
			} catch (DiceException e) {
				logger.error("Dice Roll Failed: " + e.getMessage());
			}

		}
	}

}
