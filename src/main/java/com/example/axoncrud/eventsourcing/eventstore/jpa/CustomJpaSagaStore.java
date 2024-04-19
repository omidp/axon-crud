package com.example.axoncrud.eventsourcing.eventstore.jpa;

import jakarta.persistence.EntityManager;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.modelling.saga.AssociationValue;
import org.axonframework.modelling.saga.repository.jpa.AssociationValueEntry;
import org.axonframework.modelling.saga.repository.jpa.JpaSagaStore;
import org.axonframework.modelling.saga.repository.jpa.SagaEntry;
import org.axonframework.serialization.Serializer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class CustomJpaSagaStore extends JpaSagaStore {

	private static final String SAGA_ID_PARAM = "sagaId";
	private static final String SAGA_TYPE_PARAM = "sagaType";
	private static final String FIND_ASSOCIATIONS_NAMED_QUERY = "FIND_ASSOCIATIONS_NAMED_QUERY";
	private final Serializer serializer;

	protected CustomJpaSagaStore(Builder builder) {
		super(builder);
		this.serializer = builder.serializer.get();
	}

	@Override
	protected String sagaEntryEntityName() {
		return SagaEntry.class.getSimpleName();
	}

	@Override
	protected org.axonframework.modelling.saga.repository.jpa.SagaEntry<?> createSagaEntry(Object saga, String sagaIdentifier, Serializer serializer) {
		return super.createSagaEntry(saga, sagaIdentifier, serializer);
	}

	@Override
	protected void storeAssociationValue(EntityManager entityManager, Class<?> sagaType, String sagaIdentifier, AssociationValue associationValue) {
		entityManager.persist(new AssociationValueEntry(getSagaTypeName(sagaType), sagaIdentifier, associationValue));
	}


	private String getSagaTypeName(Class<?> sagaType) {
		return serializer.typeForClass(sagaType).getName();
	}

	protected Set<AssociationValue> loadAssociationValues(EntityManager entityManager, Class<?> sagaType,
														  String sagaIdentifier) {
		List<org.axonframework.modelling.saga.repository.jpa.AssociationValueEntry> associationValueEntries =
			entityManager.createNamedQuery(FIND_ASSOCIATIONS_NAMED_QUERY, org.axonframework.modelling.saga.repository.jpa.AssociationValueEntry.class)
				.setParameter(SAGA_TYPE_PARAM, getSagaTypeName(sagaType))
				.setParameter(SAGA_ID_PARAM, sagaIdentifier)
				.getResultList();

		return associationValueEntries.stream().map(org.axonframework.modelling.saga.repository.jpa.AssociationValueEntry::getAssociationValue)
			.collect(Collectors.toCollection(HashSet::new));
	}


	public static CustomJpaSagaStore.Builder builder() {
		return new CustomJpaSagaStore.Builder();
	}

	public static class Builder extends JpaSagaStore.Builder {
		private Supplier<Serializer> serializer;

		@Override
		public CustomJpaSagaStore build() {
			return new CustomJpaSagaStore(this);
		}

		@Override
		public CustomJpaSagaStore.Builder serializer(Serializer serializer) {
			super.serializer(serializer);
			this.serializer = () -> serializer;
			return this;
		}

		@Override
		public CustomJpaSagaStore.Builder entityManagerProvider(EntityManagerProvider entityManagerProvider) {
			super.entityManagerProvider(entityManagerProvider);
			return this;
		}

	}

}
