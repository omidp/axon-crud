package com.example.axoncrud.command;


import com.example.axoncrud.aggregate.GiftCard;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.Repository;

@RequiredArgsConstructor
public class GiftCardCommandHandler {
	private final Repository<GiftCard> giftCardRepository;

	@CommandHandler
	public void handle(IssueCardCommand cmd) {
		System.out.println(cmd.toString());
		try {
			giftCardRepository.newInstance(() -> new GiftCard(cmd));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@CommandHandler
	public void handle(IssueCardUpdateCommand cmd) {
		System.out.println(cmd.toString());
		giftCardRepository.load(cmd.getCardId().toString()).execute(giftCard -> giftCard.update(cmd));

	}

}
