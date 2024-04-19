package com.example.axoncrud.saga;

import com.example.axoncrud.command.UserCreateCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;

public class UserSaga {


	@StartSaga
	@SagaEventHandler(associationProperty = "userId")
	public void createUser(ExternalServiceUserCreatedEvent userCreatedEvent, CommandGateway commandGateway){
		System.out.println("createUser " + userCreatedEvent.toString());
		commandGateway.sendAndWait(new UserCreateCommand(userCreatedEvent.getUserId(), userCreatedEvent.getName()));
	}


	@SagaEventHandler(associationProperty = "userId")
	@EndSaga
	public void shipOrderToUser(UserCreatedEvent userCreatedEvent, CommandGateway commandGateway){
		System.out.println("ship to " + userCreatedEvent.toString());
	}

}
