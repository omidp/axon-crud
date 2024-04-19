package com.example.axoncrud.event;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.annotation.MetaDataValue;

public class GiftCardEventHandler {

	@EventHandler
	public void eventHandler(CardIssuedEvent issuedEvent, @MetaDataValue(value = "username", required = false) String username) {
		System.out.println("username event : " + username);
		System.out.println(issuedEvent.toString());
	}

}
