package com.recursivechaos.chaosbot.listeners.redditpreview;

/**
 * PreviewException will handle all exceptions that the bot may throw during runtime
 * 
 * @author Andrew Bell www.recursivechaos.com
 */
public class PreviewException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public PreviewException() { super(); }
	public PreviewException(String message) { super(message); }
	public PreviewException(String message, Throwable cause) { super(message, cause); }
	public PreviewException(Throwable cause) { super(cause); }

}
