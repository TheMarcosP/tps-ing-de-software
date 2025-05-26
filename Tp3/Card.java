package uno;

public abstract class Card {
    protected String color;
    private boolean unoCalled = false;

    public static String Red = "Red";
    public static String Blue = "Blue";
    public static String Green = "Green";
    public static String Yellow = "Yellow";

    public abstract boolean equals(Object other);
    abstract boolean canBePlayedOn(Card other);
    boolean matchesColor(String color) { return this.color.equals(color); }
    boolean matchesNumber(int number) { return false; }
    boolean matchesSymbol(SpecialCard other) { return false; }
    boolean getUno() { return unoCalled; }

    Card callUno() {
        unoCalled = true;
        return this;
    }

    Card updateGameState(UnoGame game) {
        game.nextTurn();
        return this;
    }
}
