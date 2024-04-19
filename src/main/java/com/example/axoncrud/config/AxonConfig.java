package com.example.axoncrud.config;

import com.example.axoncrud.aggregate.GiftCard;
import com.example.axoncrud.aggregate.User;
import com.example.axoncrud.command.GiftCardCommandHandler;
import com.example.axoncrud.command.UserCommandHandler;
import com.example.axoncrud.domain.GiftCardEntity;
import com.example.axoncrud.event.GiftCardEventHandler;
import com.example.axoncrud.eventsourcing.eventstore.jpa.CustomJpaEventStorageEngine;
import com.example.axoncrud.eventsourcing.eventstore.jpa.SystemAuditDataProvider;
import com.example.axoncrud.eventsourcing.eventstore.jpa.TenantAwareDomainEventEntry;
import com.example.axoncrud.saga.UserSaga;
import com.thoughtworks.xstream.XStream;
import jakarta.persistence.EntityManagerFactory;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.deadletter.jpa.DeadLetterEntry;
import org.axonframework.eventhandling.deadletter.jpa.JpaSequencedDeadLetterQueue;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jpa.JpaTokenStore;
import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jpa.DomainEventEntry;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.messaging.interceptors.LoggingInterceptor;
import org.axonframework.messaging.interceptors.TransactionManagingInterceptor;
import org.axonframework.modelling.command.Repository;
import org.axonframework.modelling.saga.ResourceInjector;
import org.axonframework.modelling.saga.repository.SagaStore;
import org.axonframework.modelling.saga.repository.jpa.JpaSagaStore;
import org.axonframework.modelling.saga.repository.jpa.SagaEntry;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.CompactDriver;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.axonframework.spring.saga.SpringResourceInjector;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionExecution;
import org.springframework.transaction.TransactionExecutionListener;

import javax.sql.DataSource;

import static org.springframework.transaction.support.AbstractPlatformTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION;

@Configuration
public class AxonConfig {


	@Bean
	CommandBus commandBus(TransactionManager tx) {
		SimpleCommandBus simpleCommandBus = SimpleCommandBus.builder()
			.transactionManager(tx)
			.build();
		simpleCommandBus.registerHandlerInterceptor(new TransactionManagingInterceptor(tx));
		simpleCommandBus.registerHandlerInterceptor(new LoggingInterceptor());
		return simpleCommandBus;
	}

	@Bean
	CommandGateway commandGateway(CommandBus commandBus) {
		return DefaultCommandGateway.builder()
			.commandBus(commandBus)
			.build();
	}

	@Bean
	EmbeddedEventStore eventStore(EventStorageEngine eventStorageEngine) {
		EmbeddedEventStore ees = EmbeddedEventStore.builder()
			.storageEngine(eventStorageEngine)
			.build();
		ees.registerDispatchInterceptor(new SystemAuditDataProvider());
		return ees;
	}

	@Bean
	public EventStorageEngine eventStorageEngine(TransactionManager tx, EntityManagerProvider entityManagerProvider, Serializer serializer) {
		return CustomJpaEventStorageEngine.builder()
			.transactionManager(tx)
			.eventSerializer(serializer)
			.snapshotSerializer(serializer)
			.entityManagerProvider(entityManagerProvider)
			.explicitFlush(true)
			.build();
	}


	@Bean
	public Repository<GiftCard> repositoryForGiftCard(EventStore eventStore) {
		return EventSourcingRepository.builder(GiftCard.class).eventStore(eventStore).build();
	}

	@Bean
	public Repository<User> repositoryForUser(EventStore eventStore) {
		return EventSourcingRepository.builder(User.class).eventStore(eventStore).build();
	}

	@Bean
	Serializer serializer() {
		XStream xStream = new XStream(new CompactDriver());
		xStream.autodetectAnnotations(true); // to detect @XStreamConverter and @XStreamAlias annotations
		xStream.ignoreUnknownElements();
		xStream.allowTypesByWildcard(new String[] {"com.example.axoncrud.**", "org.axonframework.**"});
		return XStreamSerializer.builder().xStream(xStream).build();
	}

	@Bean
	TokenStore jpaTokenStore(Serializer serializer, EntityManagerProvider entityManagerProvider, DataSource dataSource) {
		return JpaTokenStore.builder()
			.serializer(serializer)
			.entityManagerProvider(entityManagerProvider)
			.build();
	}

	@Bean
	SagaStore<Object> sagaStore(Serializer serializer, EntityManagerProvider entityManagerProvider){
		return JpaSagaStore.builder()
			.serializer(serializer)
			.entityManagerProvider(entityManagerProvider)
			.build();
	}

	@Bean
	ResourceInjector springResourceInjector(){
		return new SpringResourceInjector();
	}

	@Bean
	org.axonframework.config.Configuration DefaultConfigurer(CommandBus commandBus, EventStore eventStore,
															 EventStorageEngine eventStorageEngine, Serializer serializer,
															 TokenStore tokenStore, TransactionManager transactionManager,
															 SagaStore sagaStore, EntityManagerProvider entityManagerProvider, ResourceInjector springResourceInjector) {
		System.setProperty("disable-axoniq-console-message", "true");
		TrackingEventProcessorConfiguration tepConfig =
			TrackingEventProcessorConfiguration.forSingleThreadedProcessing()
				.andInitialTrackingToken(trackedEventMessageStreamableMessageSource -> trackedEventMessageStreamableMessageSource.createHeadToken())
//				.andTokenClaimInterval(1000, TimeUnit.MILLISECONDS)
//				.andEventAvailabilityTimeout(2000, TimeUnit.MILLISECONDS)
			;
		Configurer configurer = DefaultConfigurer.defaultConfiguration()
			.configureTransactionManager(configuration -> transactionManager)
			.configureResourceInjector(configuration -> springResourceInjector)
			.configureCommandBus(conf -> commandBus)
			.configureEventStore(conf -> eventStore)
			.configureEmbeddedEventStore(configuration -> eventStorageEngine)
			.configureSerializer(configuration -> serializer)
			.configureEventSerializer(configuration -> serializer)
			.configureMessageSerializer(configuration -> serializer)
			.eventProcessing(eventProcessingConfigurer -> eventProcessingConfigurer
				.registerDefaultTransactionManager(configuration -> transactionManager)
				.registerTokenStore(configuration -> tokenStore)
				.registerSagaStore(configuration -> sagaStore)
				.registerSaga(UserSaga.class)
				.registerDeadLetterQueueProvider(processingGroup -> {
					return config -> JpaSequencedDeadLetterQueue.builder()
						.processingGroup(processingGroup)
						.entityManagerProvider(entityManagerProvider)
						.transactionManager(transactionManager)
						.serializer(config.serializer())
						.build();
				})
			)
			.configureAggregate(GiftCard.class)
			.registerCommandHandler(configuration -> new GiftCardCommandHandler(repositoryForGiftCard(configuration.eventStore())))
			.registerCommandHandler(configuration -> new UserCommandHandler(repositoryForUser(configuration.eventStore())))
			.registerEventHandler(configuration -> new GiftCardEventHandler())
			;
		return configurer.start();
	}



	@Bean
	public TransactionManager axonTransactionManager(PlatformTransactionManager transactionManager, DataSource ds, EntityManagerFactory emf) {
//		return new SpringTransactionManager(transactionManager);
		return new CustomSpringTransactionManager(transactionManager, ds, emf);
	}

	@Bean
	EntityManagerProvider entityManagerProvider() {
		return new CustomEntityManagerProvider();
	}

	////

	@Bean
	PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory, DataSource ds) {
		JpaTransactionManager sourceTransactionManager = new JpaTransactionManager(entityManagerFactory);
		sourceTransactionManager.setDataSource(ds);
		sourceTransactionManager.setTransactionSynchronization(SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
		sourceTransactionManager.addListener(new TransactionExecutionListener() {
			@Override
			public void afterBegin(TransactionExecution transaction, Throwable beginFailure) {
//				System.out.println("################");
			}
		});
		return sourceTransactionManager;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaProperties jpaProps) {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(dataSource);
		jpaVendorAdapter.setGenerateDdl(true);
		jpaVendorAdapter.setShowSql(false);
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		factoryBean.setJpaPropertyMap(jpaProps.getProperties());
		factoryBean.setPackagesToScan(
			TenantAwareDomainEventEntry.class.getPackage().getName(),
			TokenEntry.class.getPackage().getName(),
			SagaEntry.class.getPackageName(),
			DeadLetterEntry.class.getPackageName());
		factoryBean.setPersistenceUnitName("persistenceUnit");
		return factoryBean;
	}

}
