package uno;

import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;

public class UnoGame {
    private Deque<Player> playersQueue = new ArrayDeque<>();
    private Deque<Card> drawPile = new ArrayDeque<>();
    private Card topCard;
    boolean gameOver = false;

    public static String WrongTurn = "Wrong turn! It's not the player's turn.";
    public static String CardNotPlayable = "Card not playable! Cannot play this card on the top card.";
    public static String GameOver = "Game over! The game has already ended.";

    public UnoGame(List<Card> cardsDeck, List<String> playerNames, int cardsPerPlayer) {
        playerNames.forEach(name -> playersQueue.add(new Player(name)));
        drawPile.addAll(cardsDeck);
        playersQueue.forEach(player -> {
            drawPile.stream().limit(cardsPerPlayer).forEach(card -> player.addCard(drawPile.pop()));
        });
        topCard = drawPile.pop();
    }

    public String getCurrentPlayerName() { return playersQueue.peek().getName(); }
    public Card getTopCard() { return topCard; }
    public boolean isGameOver() { return gameOver; }

    public UnoGame playCard(String playerName, Card card) {
        Player currentPlayer = getCurrentPlayer(playerName);
        if (!currentPlayer.hasCard(card) || !card.canBePlayedOn(topCard)) {
            throw new RuntimeException(CardNotPlayable);
        }
        gameOver = currentPlayer.removeCard(card);
        if (currentPlayer.hasOneCard() && !card.getUno()) {
            drawTwoCards();
        }
        topCard = card.updateGameState(this);
        return this;
    }

    public UnoGame drawCard(String playerName) {
        Player currentPlayer = getCurrentPlayer(playerName);
        drawPile.stream()
                .takeWhile(card -> !card.canBePlayedOn(topCard))
                .forEach(card -> currentPlayer.addCard(drawPile.pop()));
        topCard = drawPile.pop().updateGameState(this);
        return this;
    }

    UnoGame nextTurn() {
        playersQueue.add(playersQueue.poll());
        return this;
    }

    UnoGame drawTwoCards() {
        drawPile.stream()
                .limit(2)
                .forEach(playersQueue.peek()::addCard);
        return this;
    }

    UnoGame reverseTurns() {
        playersQueue = playersQueue.reversed();
        return this;
    }

    private void checkGameOver() {
        if (isGameOver()) {
            throw new RuntimeException(GameOver);
        }
    }

    private void checkTurn(String playerName) {
        if (!playersQueue.peek().getName().equals(playerName)) {
            throw new RuntimeException(WrongTurn);
        }
    }

    private Player getCurrentPlayer(String playerName) {
        checkGameOver();
        checkTurn(playerName);
        return playersQueue.peek();
    }
}
