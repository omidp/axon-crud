package com.example.axoncrud.eventsourcing.eventstore.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.axonframework.eventsourcing.eventstore.jpa.DomainEventEntry;
//
@Entity
@Table(name = "TenantAwareDomainEventEntry")
public class TenantAwareDomainEventEntry extends DomainEventEntry {

	@Getter @Setter private Integer siteNumber;

}
