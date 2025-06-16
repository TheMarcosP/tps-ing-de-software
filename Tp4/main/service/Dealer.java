package org.udesa.unoback.service;

import org.springframework.stereotype.Component;
import org.udesa.unoback.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class Dealer {
    public List<Card> fullDeck() {
        List<Card> deck = new ArrayList<>();
        deck.addAll(cardsOn("Red"));
        deck.addAll(cardsOn("Blue"));
        deck.addAll(cardsOn("Green"));
        deck.addAll(cardsOn("Yellow"));
        Collections.shuffle(deck);
        return deck;
    }

    private List<Card> cardsOn(String color) {
        return Stream.concat(Stream.of(
                        new WildCard(),
                        new SkipCard(color),
                        new Draw2Card(color),
                        new ReverseCard(color)),
                IntStream.rangeClosed(1, 9)
                        .mapToObj(n -> new NumberCard(color, n))).toList();
    }
}
