package com.example.axoncrud.eventsourcing.eventstore.jpa.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.axonframework.eventhandling.AbstractDomainEventEntry;
import org.axonframework.eventhandling.DomainEventData;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.serialization.Serializer;

//
@Entity
@Table(name = "TenantAwareDomainEventEntry")
@Data
public class TenantAwareDomainEventEntry extends AbstractDomainEventEntry<byte[]> implements DomainEventData<byte[]> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long globalIndex;

	private Integer siteNumber;

	/**
	 * Construct a new default domain event entry from a published domain event message to enable storing the event or
	 * sending it to a remote location. The event payload and metadata will be serialized to a byte array.
	 * <p>
	 * The given {@code serializer} will be used to serialize the payload and metadata in the given {@code
	 * eventMessage}. The type of the serialized data will be the same as the given {@code contentType}.
	 *
	 * @param eventMessage The event message to convert to a serialized event entry
	 * @param serializer   The serializer to convert the event
	 * @param contentType  The data type of the payload and metadata after serialization
	 */
	public TenantAwareDomainEventEntry(DomainEventMessage<?> eventMessage, Serializer serializer,
											 Class<byte[]> contentType, Integer siteNumber) {
		super(eventMessage, serializer, contentType);
		this.siteNumber = siteNumber;
	}

	/**
	 * Default constructor required by JPA
	 */
	protected TenantAwareDomainEventEntry() {
	}
}

