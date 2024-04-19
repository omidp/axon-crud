package com.example.axoncrud.aggregate;

import com.example.axoncrud.command.IssueCardCommand;
import com.example.axoncrud.command.IssueCardUpdateCommand;
import com.example.axoncrud.command.UserCreateCommand;
import com.example.axoncrud.event.CardIssuedEvent;
import com.example.axoncrud.saga.UserCreatedEvent;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;

import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

public class User {

	@AggregateIdentifier
	private UUID id;

	public User(UserCreateCommand cmd) {
		apply(new UserCreatedEvent(cmd.getUserId(), cmd.getName()));
	}


	@EventSourcingHandler // 4.
	public void on(UserCreatedEvent evt) {
		id = evt.getUserId();
	}


	protected User() {
	}

}
