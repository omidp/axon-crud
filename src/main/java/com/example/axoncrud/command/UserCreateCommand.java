package com.example.axoncrud.command;

import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class UserCreateCommand {
	private final UUID userId;
	private final String name;
}
