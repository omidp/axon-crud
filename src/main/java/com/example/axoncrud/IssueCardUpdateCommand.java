package com.example.axoncrud;

import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class IssueCardUpdateCommand {
	private final UUID cardId;
	private final BigDecimal amount;
}
