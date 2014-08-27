package com.recursivechaos.chaosbot.hibernate.objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ChatUser {
	
	@Id
	@GeneratedValue
	int userID;
	String nickname;
	
}
