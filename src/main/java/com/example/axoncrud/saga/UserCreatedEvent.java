package com.example.axoncrud.saga;

import lombok.Value;

import java.util.UUID;

@Value
public class UserCreatedEvent {

	private UUID userId;
	private String name;

}
