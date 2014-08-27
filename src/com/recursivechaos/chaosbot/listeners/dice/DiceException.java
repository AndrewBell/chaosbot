package com.recursivechaos.chaosbot.listeners.dice;

/**
 * @author Andrew Bell www.recursivechaos.com
 */
public class DiceException extends Exception {
	private static final long serialVersionUID = 1L;
	private String message;

	public DiceException() {
		super();
	}

	public DiceException(String message) {
		super(message);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
