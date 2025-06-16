package org.udesa.unoback.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.udesa.unoback.model.JsonCard;
import org.udesa.unoback.model.Match;
import org.udesa.unoback.model.Card;

import java.util.*;

@Service
public class UnoService {
    @Autowired private Dealer dealer;
    private Map<UUID, Match> sessions = new HashMap<>();
    public static String MatchIDNotFound = "Match ID not found: ";
    public static String InvalidJsonCard = "Invalid JsonCard";

    public UUID newMatch(List<String> players) {
        UUID matchID = UUID.randomUUID();
        sessions.put(matchID, Match.fullMatch(dealer.fullDeck(), players));
        return matchID;
    }

    public void play(UUID matchID, String player, JsonCard jsonCard) {
        Card card;
        try {
            card = jsonCard.asCard();
        } catch (Exception exception) {
            throw new RuntimeException(InvalidJsonCard);
        }
        getMatch(matchID).play(player, card);
    }

    public void drawCard(UUID matchID, String player) {
        getMatch(matchID).drawCard(player);
    }

    public Card activeCard(UUID matchID) {
        return getMatch(matchID).activeCard();
    }

    public List<Card> playerHand(UUID matchID) {
        return getMatch(matchID).playerHand();
    }

    private Match getMatch(UUID matchID) {
        return Optional.ofNullable(sessions.get(matchID))
                .orElseThrow(() -> new RuntimeException(MatchIDNotFound + matchID));
    }
}
