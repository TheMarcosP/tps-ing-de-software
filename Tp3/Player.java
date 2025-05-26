package uno;

import java.util.List;
import java.util.ArrayList;

class Player {
    private String name;
    private List<Card> hand = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    void addCard(Card card) {
        hand.add(card);
    }

    boolean removeCard(Card card) {
        hand.remove(card);
        return hand.isEmpty();
    }

    boolean hasCard(Card card) {
        return hand.contains(card);
    }

    boolean hasOneCard() {
        return hand.size() == 1;
    }
}
