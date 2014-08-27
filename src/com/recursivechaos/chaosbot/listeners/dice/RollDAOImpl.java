package com.recursivechaos.chaosbot.listeners.dice;

/**
 * RollDAOImpl accepts a string dice roll, and returns the rolled value.
 * For example, 1D6 will return an int between 1 and 6,
 * or 4D10 + 5 will return an int between 15 and 45.
 * 
 * @author Andrew Bell www.recursivechaos.com
 */
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RollDAOImpl implements RollDAO {
	Logger logger = LoggerFactory.getLogger(RollDAOImpl.class);

	/**
	 * getFlavorText returns any text located after the dice roll, the flavor
	 * text can then be used in a reply message.
	 * 
	 * @param message
	 *            String of the message that contains flavor text
	 * @return String of the message after the dice roll.
	 */
	@Override
	public String getFlavorText(String message) {
		String flavorText = message.substring(message.indexOf("!roll ") + 6);
		if (flavorText.contains(" ")) {
			flavorText = flavorText.substring(flavorText.indexOf(" ") + 1);
		} else {
			flavorText = "";
		}
		return flavorText;
	}

	/**
	 * This function rolls a string, but does not check for errors. Will return
	 * 0 if failure.
	 * 
	 * @param inputDiceString
	 *            Dice value as a string, such as 10D12+5
	 * @return int value from string rolled
	 */
	@Override
	public int rollFromCheckedString(String inputDiceString) {
		int total = 0;
		// Calls rollFromString, but catches, and logs any errors.
		try {
			total = rollFromString(inputDiceString);
		} catch (DiceException e) {
			logger.error("Not able to roll: " + e.getMessage());
		}
		return total;
	}

	/**
	 * This function rolls a string representation of dice. Will throw an error
	 * if not able to parse string, or fails for another reason.
	 * 
	 * @param inputDiceString
	 *            Dice value as a string, such as 10D12+5
	 * @return int value from string rolled
	 * @throws DiceException
	 *             parse failure
	 */
	@Override
	public int rollFromString(String inputDiceString) throws DiceException {
		int MAX_ROLL = 1290;
		int noRolls;
		int noSides;
		int dSpot;
		int modVal = 0;
		int modLoc = -1;
		int initRollVal = 0;
		int finalRollVal;
		String modText = "";
		String modifier = "";
		// Currently, we need to append a space to the end in order for the
		// function to determine end points.
		inputDiceString = inputDiceString.toUpperCase() + " ";
		inputDiceString = inputDiceString.substring(inputDiceString
				.indexOf(" ") + 1);
		inputDiceString = inputDiceString.substring(0,
				inputDiceString.indexOf(" "))
				+ " ";
		// find the D location first
		if (inputDiceString.contains("D")) {
			dSpot = inputDiceString.indexOf("D");
		} else {
			throw new DiceException(
					"I can't seem to find me the D. I want the D.");
		}
		// find the number of rolls
		try {
			if (dSpot == 0) {
				noRolls = 1;
			} else {
				if (inputDiceString.substring(0, 1).equals("-")) {
					throw new DiceException("I can't roll negative dice!");
				}
				noRolls = Integer.parseInt(inputDiceString.substring(0, dSpot));
				if (noRolls > MAX_ROLL) {
					throw new DiceException(
							"I don't feel like rolling that many dice.");
				}
			}
		} catch (Exception e) {
			throw new DiceException("Now you're trying to make up number!");
		}
		// find modifiers, modify string
		try {
			// Check if a modifier is present
			if (inputDiceString.contains("+")) {
				modLoc = inputDiceString.indexOf("+");
			} else if (inputDiceString.contains("-")) {
				modLoc = inputDiceString.indexOf("-");
			} else if (inputDiceString.contains("X")) {
				modLoc = inputDiceString.indexOf("X");
			}
			// If a modifier is found, modify string
			if (modLoc > -1) {
				modText = inputDiceString.substring(modLoc,
						inputDiceString.indexOf(" ", modLoc));
				modVal = Integer.parseInt(modText.substring(1));
				if (modVal > MAX_ROLL) {
					throw new DiceException(
							"A bit excessive modifier eh? You think you're clever huh?");
				}
				inputDiceString = inputDiceString.substring(0, (modLoc)) + " ";
				modifier = modText.substring(0, 1);
			}
		} catch (Exception e) {
			throw new DiceException("That modifier is a little unreasonable.");
		}
		// get sides
		try {
			noSides = Integer.parseInt(inputDiceString.substring((dSpot + 1),
					inputDiceString.indexOf(" ")));
			if (noSides < 1) {
				throw new DiceException(
						"A dice with less than 1 side really isn't a dice, is it?");
			}
		} catch (Exception e) {
			throw new DiceException(
					"You can't roll that kind of dice! Where did you even find that?");
		}
		// roll dice
		Random randomGenerator = new Random();
		for (int i = 0; i < noRolls; i++) {
			initRollVal = initRollVal + randomGenerator.nextInt(noSides) + 1;
		}
		// modify total if necessary.
		if (modifier.equalsIgnoreCase("")) {
			finalRollVal = initRollVal;
		} else if (modifier.equalsIgnoreCase("+")) {
			finalRollVal = initRollVal + modVal;
		} else if (modifier.equalsIgnoreCase("-")) {
			finalRollVal = initRollVal - modVal;
		} else if (modifier.equalsIgnoreCase("x")) {
			finalRollVal = initRollVal * modVal;
		} else {
			throw new DiceException("I can't math.");
		}
		return finalRollVal;
	}
}
