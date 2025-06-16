package org.udesa.unoback;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.udesa.unoback.controller.UnoController;
import org.udesa.unoback.model.*;
import org.udesa.unoback.service.Dealer;
import org.udesa.unoback.service.UnoService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UnoControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean Dealer dealer;
    private static String Mark = "Mark";
    private static String Segu = "Segu";
    private static String Julio = "Julio";
    private static Card Red2 = new NumberCard(ColoredCard.Red, 2);
    private static Card Red3 = new NumberCard(ColoredCard.Red, 3);
    private static Card Blue2 = new NumberCard(ColoredCard.Blue, 2);
    private static Card Wild = new WildCard();
    private static String playableCard = Red2.asJson().toString();
    private static String unplayableCardNotInHand = Red3.asJson().toString();
    private static String unplayableCardDoNotMatch = Blue2.asJson().toString();

    @BeforeEach
    public void beforeEach() {
        when(dealer.fullDeck()).thenReturn(UnoServiceTest.fullDeck());
    }

    @Test public void testCanCreateNewMatchWithValidPlayers() throws Exception {
        assertEquals(7, getPlayerHand(getNewMarkAndSeguMatch()).size());
    }

    @Test public void testCanNotCreateNewMatchWithNotEnoughPlayers() throws Exception {
        assertEquals(Match.NotEnoughPlayers,
                performErrorRequest(createNewMatch(Mark)));
    }

    @Test public void testCanPlayCard() throws Exception {
        performSuccessRequest(playCard(getNewMarkAndSeguMatch(), Mark, playableCard));
    }

    @Test public void testCanNotPlayCardMatchIDNotFound() throws Exception {
        UUID matchID = UUID.randomUUID();
        assertMatchNotFoundError(matchID, playCard(matchID, Mark, playableCard));
    }

    @Test public void testCanNotPlayCardNotPlayersTurn() throws Exception {
        UUID matchID = getNewMarkAndSeguMatch();
        assertNotPlayersTurnError(Julio, playCard(matchID, Julio, playableCard));
        assertNotPlayersTurnError(Segu, playCard(matchID, Segu, playableCard));
    }

    @Test public void testCanNotPlayCardNotACardInHand() throws Exception {
        assertPlayCardError(Match.NotACardInHand + Mark, unplayableCardNotInHand);
    }

    @Test public void testCanNotPlayCardCardDoNotMatch() throws Exception {
        assertPlayCardError(Match.CardDoNotMatch, unplayableCardDoNotMatch);
    }

    @Test public void testCanNotPlayCardInvalidJson() throws Exception {
        assertPlayCardError(UnoService.InvalidJsonCard, "{\"invalid\":\"json\"}");
    }

    @Test public void testCanDrawCard() throws Exception {
        UUID matchID = getNewMarkAndSeguMatch();
        int initialSize = getPlayerHand(matchID).size();

        performSuccessRequest(drawCard(matchID, Mark));

        List<Card> hand = getPlayerHand(matchID);
        assertEquals(initialSize + 1, hand.size());
        assertTrue(hand.contains(new NumberCard(ColoredCard.Yellow, 2)));
    }

    @Test public void testCanNotDrawCardMatchIDNotFound() throws Exception {
        UUID matchID = UUID.randomUUID();
        assertMatchNotFoundError(matchID, drawCard(matchID, Mark));
    }

    @Test public void testCanNotDrawCardNotPlayersTurn() throws Exception {
        UUID matchID = getNewMarkAndSeguMatch();
        assertNotPlayersTurnError(Julio, drawCard(matchID, Julio));
        assertNotPlayersTurnError(Segu, drawCard(matchID, Segu));
    }

    @Test public void testCanGetActiveCard() throws Exception {
        assertEquals(new NumberCard(ColoredCard.Red, 1),
                new ObjectMapper().readValue(
                        performSuccessRequest(getActiveCard(getNewMarkAndSeguMatch())),
                        JsonCard.class).asCard());
    }

    @Test public void testCanNotGetActiveCardMatchIDNotFound() throws Exception {
        UUID matchID = UUID.randomUUID();
        assertMatchNotFoundError(matchID, getActiveCard(matchID));
    }

    @Test public void testCanGetPlayerHand() throws Exception {
        List<Card> hand = getPlayerHand(getNewMarkAndSeguMatch());
        assertCardsInHand(hand,
                Red2,
                Blue2,
                Wild,
                new ReverseCard(ColoredCard.Yellow),
                new NumberCard(ColoredCard.Yellow, 3),
                new NumberCard(ColoredCard.Red, 4),
                new Draw2Card(ColoredCard.Blue)
        );
    }

    @Test public void testCanGetOtherPlayerHand() throws Exception {
        UUID matchID = getNewMarkAndSeguMatch();
        performSuccessRequest(playCard(matchID, Mark, playableCard));

        List<Card> hand = getPlayerHand(matchID);
        assertCardsInHand(hand,
                Wild,
                Red3,
                new NumberCard(ColoredCard.Yellow, 3),
                new NumberCard(ColoredCard.Blue, 3),
                new NumberCard(ColoredCard.Yellow, 5),
                new SkipCard(ColoredCard.Red),
                new NumberCard(ColoredCard.Blue, 4)
        );
    }

    @Test public void testCanNotGetPlayerHandMatchIDNotFound() throws Exception {
        UUID matchID = UUID.randomUUID();
        assertMatchNotFoundError(matchID, getPlayerHandAction(matchID));
    }

    private String performSuccessRequest(ResultActions action) throws Exception {
        return action
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private String performErrorRequest(ResultActions action) throws Exception {
        return action
                .andExpect(status().is5xxServerError())
                .andReturn().getResponse().getContentAsString();
    }

    private void assertMatchNotFoundError(UUID matchId, ResultActions action) throws Exception {
        assertEquals(UnoService.MatchIDNotFound + matchId, performErrorRequest(action));
    }

    private void assertNotPlayersTurnError(String player, ResultActions action) throws Exception {
        assertEquals(Player.NotPlayersTurn + player, performErrorRequest(action));
    }

    private void assertCardsInHand(List<Card> hand, Card... expectedCards) {
        assertEquals(expectedCards.length, hand.size());
        java.util.Arrays.stream(expectedCards).forEach(card -> assertTrue(hand.contains(card)));
    }

    private ResultActions playCard(UUID matchId, String player, String cardJson) throws Exception {
        return mockMvc.perform(post("/play/{matchID}/{player}", matchId, player)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cardJson));
    }

    private ResultActions drawCard(UUID matchId, String player) throws Exception {
        return mockMvc.perform(post("/draw/{matchID}/{player}", matchId, player));
    }

    private ResultActions getActiveCard(UUID matchId) throws Exception {
        return mockMvc.perform(get("/activecard/{matchID}", matchId));
    }

    private ResultActions getPlayerHandAction(UUID matchId) throws Exception {
        return mockMvc.perform(get("/playerhand/{matchID}", matchId));
    }

    private ResultActions createNewMatch(String... players) throws Exception {
        return mockMvc.perform(post("/newmatch")
                .param("players", players));
    }

    private UUID getNewMarkAndSeguMatch() throws Exception {
        String response = performSuccessRequest(createNewMatch(Mark, Segu));
        return UUID.fromString(new ObjectMapper().readTree(response).asText());
    }

    private List<Card> getPlayerHand(UUID matchID) throws Exception {
        String response = performSuccessRequest(getPlayerHandAction(matchID));
        return new ObjectMapper().readValue(response, new TypeReference<List<JsonCard>>() {}).stream()
                .map(JsonCard::asCard)
                .collect(Collectors.toList());
    }

    private void assertPlayCardError(String expectedError, String cardJson) throws Exception {
        UUID matchID = getNewMarkAndSeguMatch();
        assertEquals(expectedError, performErrorRequest(playCard(matchID, Mark, cardJson)));
    }
}
