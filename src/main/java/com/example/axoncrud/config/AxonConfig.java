package com.example.axoncrud.config;

import com.example.axoncrud.GiftCard;
import com.example.axoncrud.GiftCardCommandHandler;
import com.example.axoncrud.GiftCardEventHandler;
import com.thoughtworks.xstream.XStream;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.tokenstore.TokenStore;
import org.axonframework.eventhandling.tokenstore.jpa.JpaTokenStore;
import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jpa.DomainEventEntry;
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine;
import org.axonframework.modelling.command.Repository;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.CompactDriver;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class AxonConfig {


	@Bean
	CommandBus commandBus(TransactionManager tx) {
		return SimpleCommandBus.builder()
			.transactionManager(tx)
			.build();
	}

	@Bean
	CommandGateway commandGateway(CommandBus commandBus) {
		return DefaultCommandGateway.builder()
			.commandBus(commandBus)
			.build();
	}

	@Bean
	EventStore eventStore(EventStorageEngine eventStorageEngine) {
		return EmbeddedEventStore.builder()
			.storageEngine(eventStorageEngine)
			.build();
	}

	@Bean
	public EventStorageEngine eventStorageEngine(TransactionManager tx, EntityManagerProvider entityManagerProvider, Serializer serializer) {
		return JpaEventStorageEngine.builder()
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
	org.axonframework.config.Configuration DefaultConfigurer(CommandBus commandBus, EventStore eventStore,
															 EventStorageEngine eventStorageEngine, Serializer serializer,
															 TokenStore tokenStore, TransactionManager transactionManager) {
		System.setProperty("disable-axoniq-console-message", "true");
		TrackingEventProcessorConfiguration tepConfig =
			TrackingEventProcessorConfiguration.forSingleThreadedProcessing()
				.andInitialTrackingToken(trackedEventMessageStreamableMessageSource -> trackedEventMessageStreamableMessageSource.createHeadToken())
//				.andTokenClaimInterval(1000, TimeUnit.MILLISECONDS)
//				.andEventAvailabilityTimeout(2000, TimeUnit.MILLISECONDS)
			;
		Configurer configurer = DefaultConfigurer.defaultConfiguration()
			.configureCommandBus(conf -> commandBus)
			.configureEventStore(conf -> eventStore)
			.configureEmbeddedEventStore(configuration -> eventStorageEngine)
			.configureSerializer(configuration -> serializer)
			.configureEventSerializer(configuration -> serializer)
			.configureMessageSerializer(configuration -> serializer)
			.eventProcessing(eventProcessingConfigurer -> eventProcessingConfigurer
				.registerDefaultTransactionManager(configuration -> transactionManager)
				.registerTokenStore(configuration -> tokenStore))
			.configureAggregate(GiftCard.class)
			.registerCommandHandler(configuration -> new GiftCardCommandHandler(repositoryForGiftCard(configuration.eventStore())))
			.registerEventHandler(configuration -> new GiftCardEventHandler());
		return configurer.start();
	}

	@Bean
	public TransactionManager axonTransactionManager(PlatformTransactionManager transactionManager) {
		return new SpringTransactionManager(transactionManager);
	}

	@Bean
	EntityManagerProvider entityManagerProvider() {
		return new CustomEntityManagerProvider();
	}

	////

	@Bean
	PlatformTransactionManager transactionManager(DataSource ds) {
		DataSourceTransactionManager sourceTransactionManager = new DataSourceTransactionManager(ds);
		return sourceTransactionManager;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaProperties jpaProps) {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(dataSource);
		jpaVendorAdapter.setGenerateDdl(true);
		jpaVendorAdapter.setShowSql(true);
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
		factoryBean.setJpaPropertyMap(jpaProps.getProperties());
		factoryBean.setPackagesToScan(DomainEventEntry.class.getPackage().getName(), TokenEntry.class.getPackage().getName());
		factoryBean.setPersistenceUnitName("persistenceUnit");
		return factoryBean;
	}

}
