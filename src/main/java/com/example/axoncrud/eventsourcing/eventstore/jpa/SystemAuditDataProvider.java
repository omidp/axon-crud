package com.example.axoncrud.eventsourcing.eventstore.jpa;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class SystemAuditDataProvider implements MessageDispatchInterceptor<EventMessage<?>> {

	@Override
	public BiFunction<Integer, EventMessage<?>, EventMessage<?>> handle(List<? extends EventMessage<?>> messages) {
		return (index, event) -> {
			return event.andMetaData(Map.of("username", "omid"));
		};
	}
}