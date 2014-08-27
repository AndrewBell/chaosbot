package com.recursivechaos.chaosbot.exceptions;

/**
 * SnoopException will handle all exceptions that the bot may throw during runtime
 * 
 * @author Andrew Bell www.recursivechaos.com
 */
public class SnoopException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public SnoopException() { super(); }
	public SnoopException(String message) { super(message); }
	public SnoopException(String message, Throwable cause) { super(message, cause); }
	public SnoopException(Throwable cause) { super(cause); }

}
