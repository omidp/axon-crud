package com.example.axoncrud.aggregate;

import com.example.axoncrud.event.CardIssuedEvent;
import com.example.axoncrud.command.IssueCardUpdateCommand;
import com.example.axoncrud.command.IssueCardCommand;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;

import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

public class GiftCard {

    @AggregateIdentifier
    private UUID id;

    public GiftCard(IssueCardCommand cmd) {
        apply(new CardIssuedEvent(cmd.getCardId(), cmd.getAmount()));
    }


    @EventSourcingHandler // 4.
    public void on(CardIssuedEvent evt) {
        id = evt.getCardId();
    }

    public void update(IssueCardUpdateCommand cmd) {
        apply(new CardIssuedEvent(cmd.getCardId(), cmd.getAmount()));
    }

    protected GiftCard() {
    }
}