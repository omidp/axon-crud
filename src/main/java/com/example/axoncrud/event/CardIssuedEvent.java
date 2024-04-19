package com.example.axoncrud.event;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class CardIssuedEvent {
	private final UUID cardId;
	private final BigDecimal amount;
}
