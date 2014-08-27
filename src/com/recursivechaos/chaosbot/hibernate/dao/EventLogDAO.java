package com.recursivechaos.chaosbot.hibernate.dao;

/**
 * EventLogDAO defines the functions required for eventlog. Impl files should be
 * placed in rcbot.plugins.persistance.*.dao
 * 
 * @author Andrew Bell www.recursivechaos.com
 * 
 */
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;

public interface EventLogDAO {

	public void logEvent(Event<PircBotX> event) throws Exception;
}
