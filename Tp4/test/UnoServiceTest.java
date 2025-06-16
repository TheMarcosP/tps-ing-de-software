package org.udesa.unoback;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.udesa.unoback.model.*;
import org.udesa.unoback.service.Dealer;
import org.udesa.unoback.service.UnoService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UnoServiceTest {
    @Autowired private UnoService unoService;
    @MockBean Dealer dealer;
    private static String Mark = "Mark";
    private static String Segu = "Segu";
    private static List<String> Players = List.of(Mark, Segu);
    private static int InitialHandSize = 7;
    private static NumberCard Red2 = new NumberCard(ColoredCard.Red, 2);
    private static NumberCard Red3 = new NumberCard(ColoredCard.Red, 3);
    private static NumberCard Red4 = new NumberCard(ColoredCard.Red, 4);
    private static NumberCard Blue4 = new NumberCard(ColoredCard.Blue, 4);
    private static JsonCard InvalidJsonCard = new JsonCard("invalidColor", -1, "invalidType", true);

    @BeforeEach
    void beforeEach() {
        when(dealer.fullDeck()).thenReturn(fullDeck());
    }

    @Test
    public void testCanCreateNewMatches() {
        UUID matchID1 = createMatch();
        UUID matchID2 = createMatch();
        assertNotNull(matchID1);
        assertNotNull(matchID2);
        assertNotEquals(matchID1, matchID2);
        assertEquals(unoService.playerHand(matchID1), unoService.playerHand(matchID2));
    }

    @Test
    public void testCanPlayTwoMatches() {
        UUID matchID1 = createMatch();
        UUID matchID2 = createMatch();
        assertEquals(unoService.playerHand(matchID1), unoService.playerHand(matchID2));
        unoService.play(matchID1, Mark, Red2.asJson());
        assertNotEquals(unoService.playerHand(matchID1), unoService.playerHand(matchID2));
        unoService.play(matchID2, Mark, Red4.asJson());
        assertEquals(unoService.playerHand(matchID1), unoService.playerHand(matchID2));
        unoService.play(matchID1, Segu, Red3.asJson());
        assertNotEquals(unoService.playerHand(matchID1), unoService.playerHand(matchID2));
        unoService.play(matchID2, Segu, Blue4.asJson());
        assertNotEquals(unoService.playerHand(matchID1), unoService.playerHand(matchID2));
    }

    @Test
    public void testCanNotPlayMatchIDNotFound() {
        UUID nonExistentMatchID = UUID.randomUUID();
        assertMatchNotFoundError(() -> unoService.play(nonExistentMatchID, Mark, Red2.asJson()), nonExistentMatchID);
    }

    @Test
    public void testCanNotPlayInvalidJsonCard() {
        UUID matchID = createMatch();
        assertInvalidJsonCardError(() -> unoService.play(matchID, Mark, InvalidJsonCard));
    }

    @Test
    public void testCanDrawCardInTwoMatches() {
        UUID matchID1 = createMatch();
        UUID matchID2 = createMatch();
        int initialSize1 = getHandSize(matchID1);
        int initialSize2 = getHandSize(matchID2);
        unoService.drawCard(matchID1, Mark);
        assertEquals(initialSize1 + 1, getHandSize(matchID1));
        assertEquals(initialSize2, getHandSize(matchID2));
    }

    @Test
    public void testCanNotDrawCardMatchIDNotFound() {
        UUID nonExistentMatchID = UUID.randomUUID();
        assertMatchNotFoundError(() -> unoService.drawCard(nonExistentMatchID, Mark), nonExistentMatchID);
    }

    @Test
    public void testCanGetActiveCardInTwoMatches() {
        UUID matchID1 = createMatch();
        UUID matchID2 = createMatch();
        Card activeCard1 = unoService.activeCard(matchID1);
        Card activeCard2 = unoService.activeCard(matchID2);
        assertEquals(activeCard1, activeCard2);
        unoService.play(matchID1, Mark, Red2.asJson());
        assertNotEquals(activeCard1, unoService.activeCard(matchID1));
        assertNotEquals(unoService.activeCard(matchID1), unoService.activeCard(matchID2));
    }

    @Test
    public void testCanNotGetActiveCardMatchIDNotFound() {
        UUID nonExistentMatchID = UUID.randomUUID();
        assertMatchNotFoundError(() -> unoService.activeCard(nonExistentMatchID), nonExistentMatchID);
    }

    @Test
    public void testCanGetPlayerHandInTwoMatches() {
        UUID matchID1 = createMatch();
        UUID matchID2 = createMatch();
        List<Card> playerHand1 = unoService.playerHand(matchID1);
        List<Card> playerHand2 = unoService.playerHand(matchID2);
        assertEquals(playerHand1, playerHand2);
        unoService.drawCard(matchID1, Mark);
        assertNotEquals(playerHand1, unoService.playerHand(matchID1));
        assertEquals(playerHand2, unoService.playerHand(matchID2));
    }

    @Test
    public void testCanNotGetPlayerHandMatchIDNotFound() {
        UUID nonExistentMatchID = UUID.randomUUID();
        assertMatchNotFoundError(() -> unoService.playerHand(nonExistentMatchID), nonExistentMatchID);
    }

    @Test
    public void testNewMatchTest() {
        UUID matchID = createMatch();
        assertNotNull(matchID);
        assertEquals(InitialHandSize, getHandSize(matchID));
    }

    @Test
    public void testDrawCardTest() {
        UUID matchID = createMatch();
        int initialSize = getHandSize(matchID);
        unoService.drawCard(matchID, Mark);
        assertEquals(initialSize + 1, getHandSize(matchID));
    }

    @Test
    public void testPlayCardTest() {
        UUID matchID = createMatch();
        unoService.drawCard(matchID, Mark);
        unoService.play(matchID, Mark, Red2.asJson());
        assertEquals(Red2, unoService.activeCard(matchID));
        assertEquals(InitialHandSize, getHandSize(matchID));
    }

    private UUID createMatch() {
        return unoService.newMatch(Players);
    }

    private int getHandSize(UUID matchID) {
        return unoService.playerHand(matchID).size();
    }

    private void assertMatchNotFoundError(Runnable action, UUID expectedMatchID) {
        String expectedMessage = UnoService.MatchIDNotFound + expectedMatchID;
        RuntimeException exception = assertThrows(RuntimeException.class, action::run);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private void assertInvalidJsonCardError(Runnable action) {
        RuntimeException exception = assertThrows(RuntimeException.class, action::run);
        assertEquals(UnoService.InvalidJsonCard, exception.getMessage());
    }

    public static List<Card> fullDeck() {
        return List.of(
                new NumberCard(ColoredCard.Red, 1),
                Red2,
                Red4,
                new NumberCard(ColoredCard.Blue, 2),
                new WildCard(),
                new ReverseCard(ColoredCard.Yellow),
                new NumberCard(ColoredCard.Yellow, 3),
                new Draw2Card(ColoredCard.Blue),
                Red3,
                Blue4,
                new NumberCard(ColoredCard.Yellow, 3),
                new NumberCard(ColoredCard.Blue, 3),
                new WildCard(),
                new NumberCard(ColoredCard.Yellow, 5),
                new SkipCard(ColoredCard.Red),
                new NumberCard(ColoredCard.Yellow, 2),
                new NumberCard(ColoredCard.Green, 1),
                new NumberCard(ColoredCard.Red, 5));
    }
}
