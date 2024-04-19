package com.example.axoncrud.command;

import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class IssueCardCommand {
	private final UUID cardId;
	private final BigDecimal amount;
}
