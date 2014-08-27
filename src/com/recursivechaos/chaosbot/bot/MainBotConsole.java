package com.recursivechaos.chaosbot.bot;

import org.pircbotx.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

import com.recursivechaos.chaosbot.bot.objects.BotConfiguration;
import com.recursivechaos.chaosbot.bot.objects.BotTools;

public class MainBotConsole {
	static Logger log = LoggerFactory.getLogger(MainBotConsole.class);

	  
	
	public static void main(String[] args){
		// assume SLF4J is bound to logback in the current environment
	    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
	    // print logback's internal status
	    StatusPrinter.print(lc);
	    log.debug("Console loaded.");
		
		BotConfiguration botConfig = BotTools.loadBotConfig(args);
		Configuration<?> config = BotTools.buildConfiguration(botConfig);
		BotTools.startBot(config);

	}
	
	
}
