package com.recursivechaos.chaosbot.listeners.stoopsnoop;

/**
 * LogListener listens for events and passes them to the EventLogDAO
 * 
 * @author Andrew Bell www.recursivechaos.com
 * 
 */

import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.recursivechaos.chaosbot.hibernate.dao.EventLogDAO;
import com.recursivechaos.chaosbot.hibernate.dao.EventLogDAOImpl;

public class LogListener extends ListenerAdapter<PircBotX> {
	Logger logger = LoggerFactory.getLogger(LogListener.class);
	EventLogDAO eventlog = new EventLogDAOImpl();

	@Override
	public void onEvent(final Event<PircBotX> event) throws Exception {
		try{
			eventlog.logEvent(event);
		}catch(Exception e){
			//String admin = (event.getBot().getSettings().getAdmin());
			//event.getBot().sendIRC().message(admin, "I broke: " + e.getMessage());
		}finally{
			// This throws a generic Exception, but in *theory* the SnoopException will be caught,
			// while anything else will be passed on to the bot to handle ever so gracefully.
			super.onEvent(event);
		}
	}
}