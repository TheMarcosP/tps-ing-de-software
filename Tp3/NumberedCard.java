package uno;

class NumberedCard extends Card {
    private int number;

    public NumberedCard(String color, int number) {
        this.color = color;
        this.number = number;
    }

    public boolean equals(Object other) {
        Card otherCard = (Card) other;
        return otherCard.matchesColor(color) && otherCard.matchesNumber(number);
    }

    boolean canBePlayedOn(Card other) { return other.matchesColor(color) || other.matchesNumber(number); }
    boolean matchesNumber(int number) { return this.number == number; }
}
