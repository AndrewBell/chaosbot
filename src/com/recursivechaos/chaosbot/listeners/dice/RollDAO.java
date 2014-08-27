package com.recursivechaos.chaosbot.listeners.dice;

/**
 * RollDAO is the interface for RollDAOImpl
 * 
 * @author Andrew Bell www.recursivechaos.com
 */
public interface RollDAO {
	public String getFlavorText(String message);

	public int rollFromCheckedString(String string);

	public int rollFromString(String inputDiceString) throws DiceException;
}