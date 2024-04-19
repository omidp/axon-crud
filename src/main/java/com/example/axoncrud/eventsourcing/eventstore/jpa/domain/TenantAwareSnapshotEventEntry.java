package com.example.axoncrud.eventsourcing.eventstore.jpa.domain;

import jakarta.persistence.Entity;
import lombok.Data;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.AbstractSnapshotEventEntry;
import org.axonframework.serialization.Serializer;

@Entity
@Data
public class TenantAwareSnapshotEventEntry extends AbstractSnapshotEventEntry<byte[]> {

	private Integer siteNumber;

	/**
	 * Construct a new default snapshot event entry from an aggregate. The snapshot payload and metadata will be
	 * serialized to a byte array.
	 * <p>
	 * The given {@code serializer} will be used to serialize the payload and metadata in the given {@code
	 * eventMessage}. The type of the serialized data will be the same as the given {@code contentType}.
	 *
	 * @param eventMessage The snapshot event message to convert to a serialized event entry
	 * @param serializer   The serializer to convert the snapshot event
	 */
	public TenantAwareSnapshotEventEntry(DomainEventMessage<?> eventMessage, Serializer serializer, Integer siteNumber) {
		super(eventMessage, serializer, byte[].class);
		this.siteNumber = siteNumber;
	}

	/**
	 * Default constructor required by JPA
	 */
	protected TenantAwareSnapshotEventEntry() {
	}
}