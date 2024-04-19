package com.example.axoncrud.eventsourcing.eventstore.jpa;

import org.axonframework.eventsourcing.eventstore.jpa.DomainEventEntry;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;

public class CustomJpaEventStorageEngine extends JpaEventStorageEngine {

	protected CustomJpaEventStorageEngine(Builder builder) {
		super(builder);
	}


	@Override
	protected String domainEventEntryEntityName() {
		return TenantAwareDomainEventEntry.class.getSimpleName();
	}
}
