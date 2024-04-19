package com.example.axoncrud.command;


import com.example.axoncrud.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.Repository;

@RequiredArgsConstructor
public class UserCommandHandler {
	private final Repository<User> userRepository;

	@CommandHandler
	public void handle(UserCreateCommand cmd) {
		System.out.println(cmd.toString());
		try {
			userRepository.newInstance(() -> new User(cmd));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}



}
