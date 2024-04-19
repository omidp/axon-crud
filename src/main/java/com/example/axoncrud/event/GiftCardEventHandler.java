package com.example.axoncrud.event;

import org.axonframework.eventhandling.EventHandler;

public class GiftCardEventHandler {

	@EventHandler
	public void eventHandler(CardIssuedEvent issuedEvent){
		System.out.println(issuedEvent.toString());
	}

}
