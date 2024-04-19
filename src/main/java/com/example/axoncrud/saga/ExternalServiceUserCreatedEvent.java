package com.example.axoncrud.saga;

import lombok.Value;

import java.util.UUID;

@Value
public class ExternalServiceUserCreatedEvent {

	private UUID userId;
	private String name;

}
