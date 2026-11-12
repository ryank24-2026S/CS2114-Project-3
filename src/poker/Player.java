package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {

    private final String playerId;
    private final String username;
    private int chipBalance;
    private final List<Card> holeCards;
    private int currentBet;
    private boolean hasFolded;
    private boolean hasActed;

    public Player(String playerId, String username, int chipBalance) {
        if (playerId == null || playerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid player ID.");
        }

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid username.");
        }

        if (chipBalance < 0) {
            throw new IllegalArgumentException("Invalid chip balance.");
        }

        this.playerId = playerId;
        this.username = username;
        this.chipBalance = chipBalance;
        this.holeCards = new ArrayList<>();
        this.currentBet = 0;
        this.hasFolded = false;
        this.hasActed = false;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public int getChipBalance() {
        return chipBalance;
    }

    public List<Card> getHoleCards() {
        return Collections.unmodifiableList(
            new ArrayList<>(holeCards));
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public boolean hasFolded() {
        return hasFolded;
    }

    public boolean hasActed() {
        return hasActed;
    }

    public boolean isAllIn() {
        return chipBalance == 0;
    }

    public void addCard(Card card) {
        if (card == null || holeCards.size() >= 2) {
            throw new IllegalArgumentException(
                "Cannot add another hole card.");
        }

        holeCards.add(card);
    }

    public void deductChips(int amount) {
        if (amount <= 0 || amount > chipBalance) {
            throw new IllegalArgumentException(
                "Invalid chip amount.");
        }

        chipBalance -= amount;
        currentBet += amount;
    }

    public void fold() {
        hasFolded = true;
    }

    public void setHasActed(boolean hasActed) {
        this.hasActed = hasActed;
    }

    public void resetBet() {
        currentBet = 0;
        hasActed = false;
    }

    public void resetForNewHand() {
        holeCards.clear();
        currentBet = 0;
        hasFolded = false;
        hasActed = false;
    }

    public void addWinnings(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                "Winnings cannot be negative.");
        }

        chipBalance += amount;
    }
}