package com.example.axoncrud.event;

import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.annotation.MetaDataValue;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GiftCardEventHandler {

	@EventHandler
	public void eventHandler(CardIssuedEvent issuedEvent, @MetaDataValue(value = "username", required = false) String username) {
		System.out.println("username event : " + username);
		System.out.println(issuedEvent.toString());
//		entityManager.persist(new GiftCardEntity(issuedEvent.getCardId(), issuedEvent.getAmount()));
	}

}
