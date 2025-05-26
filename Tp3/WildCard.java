package uno;

class WildCard extends Card {
    public boolean equals(Object other) { return other.getClass().getName().equals(getClass().getName()); }
    boolean canBePlayedOn(Card other) { return true; }

    WildCard asRed() {
        this.color = Red;
        return this;
    }

    WildCard asBlue() {
        this.color = Blue;
        return this;
    }

    WildCard asGreen() {
        this.color = Green;
        return this;
    }

    WildCard asYellow() {
        this.color = Yellow;
        return this;
    }
}
