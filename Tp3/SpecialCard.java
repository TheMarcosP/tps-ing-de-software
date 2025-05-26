package uno;

class SpecialCard extends Card {
    public SpecialCard(String color) { this.color = color; }

    public boolean equals(Object other) {
        Card otherCard = (Card) other;
        return otherCard.matchesColor(color) && otherCard.matchesSymbol(this);
    }

    boolean canBePlayedOn(Card other) { return other.matchesColor(color) || other.matchesSymbol(this); }
    boolean matchesSymbol(SpecialCard other) { return other.getClass().getName().equals(getClass().getName()); }
}

class SkipCard extends SpecialCard {
    public SkipCard(String color) { super(color); }

    Card updateGameState(UnoGame game) {
        game.nextTurn().nextTurn();
        return this;
    }
}

class DrawTwoCard extends SpecialCard {
    public DrawTwoCard(String color) { super(color); }

    Card updateGameState(UnoGame game) {
        game.nextTurn().drawTwoCards().nextTurn();
        return this;
    }
}

class ReverseCard extends SpecialCard {
    public ReverseCard(String color) { super(color); }

    Card updateGameState(UnoGame game) {
        game.reverseTurns();
        return this;
    }
}
