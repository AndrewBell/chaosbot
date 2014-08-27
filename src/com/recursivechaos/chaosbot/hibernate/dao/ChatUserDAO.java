package com.recursivechaos.chaosbot.hibernate.dao;

import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import com.recursivechaos.chaosbot.hibernate.objects.ChatUser;

public class ChatUserDAO extends DAO{

	/**
	 * Return the user that triggered event
	 * @param user
	 * @return
	 */
	public static ChatUser getUser(User user) {
		
		return null;
	}

	/**
	 * Returns the user that was targeted in the message, following the command
	 * @param event
	 * @return
	 */
	public static ChatUser getTargetedUser(MessageEvent<PircBotX> event) {
		String message = event.getMessage();
		String[] splitMessage = message.split(" ");
		ChatUser user = getUser(splitMessage[1]);
		return user;
	}

	/**
	 * Looks up user by string name
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static ChatUser getUser(String name) {
		Query query = getSession().createQuery("from ChatUser where nickname = :nick ");
		query.setParameter("nick", name);
		List<ChatUser> list = Collections.emptyList(); 
		list = (List<ChatUser>) query.list();
		return list.get(0);
	}

	/**
	 * Increment hipster point counter
	 * @param hipster
	 */
	public static void addHipsterPoint(ChatUser hipster) {
		// TODO Auto-generated method stub
		
	}

}
