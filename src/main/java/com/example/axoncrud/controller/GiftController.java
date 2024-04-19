package com.example.axoncrud.controller;

import com.example.axoncrud.IssueCardCommand;
import com.example.axoncrud.IssueCardUpdateCommand;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GiftController {

	private final CommandGateway commandGateway;

	@PostMapping("/gift")
	public String create(){
		commandGateway.sendAndWait(new IssueCardCommand(UUID.randomUUID(), BigDecimal.TEN));
		return "ok";
	}

	@PutMapping("/gift/{id}")
	public String update(@PathVariable("id") UUID id){
		commandGateway.sendAndWait(new IssueCardUpdateCommand(id, BigDecimal.ONE));
		return "ok";
	}


}
