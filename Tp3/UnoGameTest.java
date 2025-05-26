package uno;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UnoGameTest {

    private NumberedCard red0, red1, red2, red3, red4, red5, red6, red7, red8, red9, green0, green1;
    private SkipCard redSkip, greenSkip;
    private ReverseCard redReverse, greenReverse;
    private DrawTwoCard redDrawTwo, greenDrawTwo;
    private WildCard wild;
    private UnoGame game;

    private static String Mark = "Mark";
    private static String Segu = "Segu";
    private static String Robbie = "Robbie";

    @BeforeEach
    public void setUp() {
        red0 = NumberedRed(0);
        red1 = NumberedRed(1);
        red2 = NumberedRed(2);
        red3 = NumberedRed(3);
        red4 = NumberedRed(4);
        red5 = NumberedRed(5);
        red6 = NumberedRed(6);
        red7 = NumberedRed(7);
        red8 = NumberedRed(8);
        red9 = NumberedRed(9);
        green0 = NumberedGreen(0);
        green1 = NumberedGreen(1);
        redSkip = SkipRed();
        redReverse = ReverseRed();
        redDrawTwo = DrawTwoRed();
        greenSkip = SkipGreen();
        greenReverse = ReverseGreen();
        greenDrawTwo = DrawTwoGreen();
        wild = Wild();

        game = trivialGame();

    }

    @Test public void testTopCard() {
        assertEquals(trivialGame().getTopCard(), red4);
    }

    @Test public void testValidCards() {
        UnoGame game = new UnoGame(
                Arrays.asList(green0, red1, green1, red2, red0),
                Arrays.asList(Mark, Segu), 2
        );
        assertDoesNotThrow(() -> game.playCard(Mark, green0), UnoGame.CardNotPlayable);
        assertDoesNotThrow(() -> game.playCard(Segu, green1), UnoGame.CardNotPlayable);
    }

    @Test public void testTurns() {
        assertEquals(Mark, game.getCurrentPlayerName());
        assertThrowsLike(() -> game.playCard(Segu, red2), UnoGame.WrongTurn);
        game.playCard(Mark, red0);
        assertEquals(Segu, game.getCurrentPlayerName());
        assertThrowsLike(() -> game.playCard(Mark, red1), UnoGame.WrongTurn);
    }

    @Test public void testInvalidCard() {
        assertThrowsLike(() -> trivialGame().playCard(Mark, red2), UnoGame.CardNotPlayable);
    }

    @Test public void testDifferentCardInstancesAreEqualNumbered() {
        UnoGame game =  new UnoGame(
                Arrays.asList(red0, red1, redSkip, wild, red4),
                Arrays.asList(Mark, Segu), 2
        );
        assertDoesNotThrow(() -> game.playCard(Mark, NumberedRed(1)), UnoGame.CardNotPlayable);
        assertDoesNotThrow(() -> game.playCard(Segu, SkipRed()), UnoGame.CardNotPlayable);
        assertDoesNotThrow(() -> game.playCard(Segu, Wild()), UnoGame.CardNotPlayable);
    }

    @Test public void testNoCalledUno() {
        UnoGame game = trivialGame()
                .playCard(Mark, red0)
                .playCard(Segu, red2)
                .playCard(Mark, red1);

        assertFalse(game.isGameOver());
    }

    @Test public void testCalledUno() {
        UnoGame game = trivialGame()
                .playCard(Mark, red0.callUno())
                .playCard(Segu, red2.callUno())
                .playCard(Mark, red1);

        assertTrue(game.isGameOver());
    }

    @Test public void testDrawCardsUntilIsPlayable() {
        UnoGame game = new UnoGame(
                Arrays.asList(red0, red1, red2, red3, green0, red4, greenSkip, greenSkip, red8),
                Arrays.asList(Mark, Segu), 2
        )
                .drawCard(Mark);

        assertEquals(greenSkip, game.getTopCard());
    }

    @Test public void testSkip() {
        UnoGame game = new UnoGame(
                Arrays.asList(redSkip, red1, red2, red3, red4),
                Arrays.asList(Mark, Segu), 2
        ).playCard(Mark, redSkip.callUno());

        assertEquals(Mark, game.getCurrentPlayerName());
    }

    @Test public void testSkipOnTopOfSkip() {
        UnoGame game = new UnoGame(
                Arrays.asList(redSkip, greenSkip, red2, red3, red4, red5, red6),
                Arrays.asList(Mark, Segu), 3
        ).playCard(Mark, redSkip.callUno());

        assertDoesNotThrow(() -> game.playCard(Mark, greenSkip.callUno()), UnoGame.CardNotPlayable);
    }

    @Test public void testReverse() {
        UnoGame game = new UnoGame(
                Arrays.asList(redReverse, red1, red2, red3, red4, red5, red6),
                Arrays.asList(Mark, Segu, Robbie), 2
        ).playCard(Mark, redReverse.callUno());

        assertEquals(Robbie, game.getCurrentPlayerName());
    }

    @Test public void testReverseOnTopOfReverse() {
        UnoGame game = new UnoGame(
                Arrays.asList(redReverse, red1, red2, red3, greenReverse, red5, red6),
                Arrays.asList(Mark, Segu, Robbie), 2
        ).playCard(Mark, redReverse.callUno());

        assertDoesNotThrow(() -> game.playCard(Robbie, greenReverse.callUno()), UnoGame.CardNotPlayable);
    }

    @Test public void testTake2() {
        UnoGame game = new UnoGame(
                Arrays.asList(redDrawTwo, red1, red2, red3, red4, red5, red6, red7, red8),
                Arrays.asList(Mark, Segu), 3
        ).playCard(Mark, redDrawTwo)
                .playCard(Mark, red1.callUno());

        assertDoesNotThrow(() -> game.playCard(Segu, red8), UnoGame.CardNotPlayable);
    }

    @Test public void testTake2OnTopOfTake2() {
        UnoGame game = new UnoGame(
                Arrays.asList(redDrawTwo, greenDrawTwo, red2, red3, red4, red5, red6, red7, red8, red9, green0),
                Arrays.asList(Mark, Segu), 3
        ).playCard(Mark, redDrawTwo);

        assertDoesNotThrow(() -> game.playCard(Mark, greenDrawTwo.callUno()), UnoGame.CardNotPlayable);
    }

    @Test public void testWildCard() {
        UnoGame game = new UnoGame(
                Arrays.asList(wild, red1, green0, red3, red4, red5, red6),
                Arrays.asList(Mark, Segu), 2
        ).playCard(Mark, wild.asGreen().callUno());

        assertEquals(wild, game.getTopCard());
        assertEquals(Segu, game.getCurrentPlayerName());
        assertThrowsLike(() -> game.playCard(Segu, red3), UnoGame.CardNotPlayable);
        game.playCard(Segu, green0.callUno());
        assertEquals(green0, game.getTopCard());
    }

    @Test public void testWildCardOnTopOfWildCard() {
        UnoGame game = new UnoGame(
                Arrays.asList(wild, red1, wild, red3, red4, red5, red6),
                Arrays.asList(Mark, Segu), 2
        ).playCard(Mark, wild.asRed().callUno());

        assertDoesNotThrow(() -> game.playCard(Segu, wild.asGreen().callUno()), UnoGame.CardNotPlayable);

    }

    @Test public void testMoreThanTwoPlayers() {
        UnoGame game = new UnoGame(
                Arrays.asList(redDrawTwo, red1, redReverse, red1, red2, redSkip, red4, red5, red6),
                Arrays.asList(Mark, Segu, Robbie), 2
        ).playCard(Mark, redDrawTwo.callUno())
                .playCard(Robbie, redSkip.callUno())
                .playCard(Segu, redReverse.callUno());

        assertEquals(redReverse, game.getTopCard());
        assertEquals(Mark, game.getCurrentPlayerName());
    }

    @Test public void testGameOver() {
        UnoGame game = new UnoGame(
                Arrays.asList(red0, red1, red2, red3, red4, red5, red6, red7, red8),
                Arrays.asList(Mark, Segu), 1
        ).playCard(Mark, red0);

        assertThrowsLike(() -> game.drawCard(Segu), UnoGame.GameOver);
        assertThrowsLike(() -> game.playCard(Segu, red1), UnoGame.GameOver);
    }

    private NumberedCard NumberedRed(int n) { return new NumberedCard(Card.Red, n); }
    private NumberedCard NumberedGreen(int n) { return new NumberedCard(Card.Green, n); }
    private DrawTwoCard DrawTwoRed() { return new DrawTwoCard(Card.Red); }
    private DrawTwoCard DrawTwoGreen() { return new DrawTwoCard(Card.Green); }
    private SkipCard SkipRed() { return new SkipCard(Card.Red); }
    private SkipCard SkipGreen() { return new SkipCard(Card.Green); }
    private ReverseCard ReverseRed() { return new ReverseCard(Card.Red); }
    private ReverseCard ReverseGreen() { return new ReverseCard(Card.Green); }
    private WildCard Wild() { return new WildCard(); }

    private UnoGame trivialGame() {
        return new UnoGame(
                Arrays.asList(red0, red1, red2, red3, red4, red5, red6, red7, red8),
                Arrays.asList(Mark, Segu), 2
        );
    }

    private void assertThrowsLike(Executable executable, String msg) {
        assertEquals(msg, assertThrows(Exception.class, executable).getMessage());
    }
}
