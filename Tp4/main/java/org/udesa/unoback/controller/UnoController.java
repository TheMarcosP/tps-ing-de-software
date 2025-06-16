package org.udesa.unoback.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.udesa.unoback.model.Card;
import org.udesa.unoback.model.JsonCard;
import org.udesa.unoback.service.UnoService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Controller
public class UnoController {
    @Autowired UnoService unoService;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleIllegalArgument(RuntimeException exception) {
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }

    @PostMapping(value = "newmatch", params = "players")
    public ResponseEntity<UUID> newMatch(@RequestParam List<String> players) {
        return ResponseEntity.ok(unoService.newMatch(players));
    }

    @PostMapping(value = "play/{matchID}/{player}", consumes = "application/json")
    public ResponseEntity<Void> play(@PathVariable UUID matchID, @PathVariable String player, @RequestBody JsonCard jsonCard) {
        unoService.play(matchID, player, jsonCard);
        return ResponseEntity.ok().build();
    }

    @PostMapping("draw/{matchID}/{player}")
    public ResponseEntity<Void> drawCard(@PathVariable UUID matchID, @PathVariable String player) {
        unoService.drawCard(matchID, player);
        return ResponseEntity.ok().build();
    }

    @GetMapping("activecard/{matchID}")
    public ResponseEntity<JsonCard> activeCard(@PathVariable UUID matchID) {
        return ResponseEntity.ok(unoService.activeCard(matchID).asJson());
    }

    @GetMapping("playerhand/{matchID}")
    public ResponseEntity<Stream<JsonCard>> playerHand(@PathVariable UUID matchID) {
        return ResponseEntity.ok(unoService.playerHand(matchID).stream().map(Card::asJson));
    }
}
