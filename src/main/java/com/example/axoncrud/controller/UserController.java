package com.example.axoncrud.controller;

import com.example.axoncrud.command.IssueCardCommand;
import com.example.axoncrud.command.IssueCardUpdateCommand;
import com.example.axoncrud.saga.ExternalServiceUserCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.GenericEventMessage;
import org.axonframework.modelling.saga.AnnotatedSagaManager;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final EventBus eventBus;

	@PostMapping("/user")
	public String create(){
		eventBus.publish(new GenericEventMessage<>(new ExternalServiceUserCreatedEvent(UUID.randomUUID(), "omid")));
		return "ok";
	}

	@PutMapping("/user/{id}")
	public String update(@PathVariable("id") UUID id){
		return "ok";
	}


}
