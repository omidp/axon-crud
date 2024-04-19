package com.example.axoncrud.eventsourcing.eventstore.jpa;

import com.example.axoncrud.eventsourcing.eventstore.jpa.domain.TenantAwareDomainEventEntry;
import com.example.axoncrud.eventsourcing.eventstore.jpa.domain.TenantAwareSnapshotEventEntry;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jpa.SnapshotEventEntry;
import org.axonframework.serialization.Serializer;

public class CustomJpaEventStorageEngine extends JpaEventStorageEngine {

	protected CustomJpaEventStorageEngine(Builder builder) {
		super(builder);
	}

	@Override
	protected String domainEventEntryEntityName() {
		return TenantAwareDomainEventEntry.class.getSimpleName();
	}

	@Override
	protected String snapshotEventEntryEntityName() {
		return TenantAwareSnapshotEventEntry.class.getSimpleName();
	}

	@Override
	protected Object createEventEntity(EventMessage<?> eventMessage, Serializer serializer) {
		return new TenantAwareDomainEventEntry(asDomainEventMessage(eventMessage), serializer, byte[].class, 1);
	}

	@Override
	protected Object createSnapshotEntity(DomainEventMessage<?> snapshot, Serializer serializer) {
		return new TenantAwareSnapshotEventEntry(snapshot, serializer, 1);
	}

	public static CustomJpaEventStorageEngine.Builder builder() {
		return new CustomJpaEventStorageEngine.Builder();
	}


	public static class Builder extends JpaEventStorageEngine.Builder {

		@Override
		public CustomJpaEventStorageEngine build() {
			return new CustomJpaEventStorageEngine(this);
		}

		@Override
		public CustomJpaEventStorageEngine.Builder transactionManager(TransactionManager transactionManager) {
			super.transactionManager(transactionManager);
			return this;
		}

		@Override
		public CustomJpaEventStorageEngine.Builder eventSerializer(Serializer eventSerializer) {
			super.eventSerializer(eventSerializer);
			return this;
		}

		@Override
		public CustomJpaEventStorageEngine.Builder snapshotSerializer(Serializer snapshotSerializer) {
			super.snapshotSerializer(snapshotSerializer);
			return  this;
		}

		@Override
		public CustomJpaEventStorageEngine.Builder entityManagerProvider(EntityManagerProvider entityManagerProvider) {
			super.entityManagerProvider(entityManagerProvider);
			return this;
		}

		@Override
		public CustomJpaEventStorageEngine.Builder explicitFlush(boolean explicitFlush) {
			super.explicitFlush(explicitFlush);
			return this;
		}
	}

}
