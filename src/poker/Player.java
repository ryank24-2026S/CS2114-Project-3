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
    private int totalBet;
    private boolean inHand;
    private boolean hasFolded;
    private boolean hasActed;
    private boolean needsTurn;

    private int lastBetFaced;

    public Player(String username, int chipBalance) {
        this(username, username, chipBalance);
    }

    // Retained for compatibility with your original Player class.
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
        this.holeCards = new ArrayList<Card>();
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return username;
    }

    public int getChipBalance() {
        return chipBalance;
    }

    public int getChips() {
        return chipBalance;
    }

    public List<Card> getHoleCards() {
        return Collections.unmodifiableList(
            new ArrayList<Card>(holeCards));
    }

    public List<Card> getCards() {
        return getHoleCards();
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public int getRoundBet() {
        return currentBet;
    }

    public int getTotalBet() {
        return totalBet;
    }

    public boolean hasFolded() {
        return hasFolded;
    }

    public boolean isFolded() {
        return hasFolded;
    }

    public boolean hasActed() {
        return hasActed;
    }

    public boolean isInHand() {
        return inHand;
    }

    public boolean isActive() {
        return inHand && !hasFolded;
    }

    public boolean isAllIn() {
        return isActive() && chipBalance == 0;
    }

    public boolean canAct() {
        return isActive() && chipBalance > 0;
    }

    public boolean needsTurn() {
        return needsTurn;
    }

    public int getLastBetFaced() {
        return lastBetFaced;
    }

    public void startHand() {
        holeCards.clear();

        currentBet = 0;
        totalBet = 0;

        inHand = chipBalance > 0;
        hasFolded = false;
        hasActed = false;
        needsTurn = false;
        lastBetFaced = 0;
    }

    public void resetForNewHand() {
        startHand();
    }

    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }

        if (!inHand) {
            throw new IllegalStateException(
                "Player is not participating in this hand.");
        }

        if (holeCards.size() >= 2) {
            throw new IllegalArgumentException(
                "Cannot add another hole card.");
        }

        if (holeCards.contains(card)) {
            throw new IllegalArgumentException(
                "Player already has that card.");
        }

        holeCards.add(card);
    }

    public void pay(int amount) {
        if (amount < 0 || amount > chipBalance) {
            throw new IllegalArgumentException("Invalid chip amount.");
        }

        chipBalance -= amount;
        currentBet += amount;
        totalBet += amount;
    }

    public void deductChips(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid chip amount.");
        }

        pay(amount);
    }

    public void refund(int amount) {
        if (amount < 0 || amount > totalBet) {
            throw new IllegalArgumentException(
                "Invalid refund amount.");
        }

        chipBalance += amount;
        totalBet -= amount;

        // Refunds must not make the current street's contribution negative.
        currentBet -= Math.min(amount, currentBet);
    }

    public void receiveChips(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                "Chip amount cannot be negative.");
        }

        chipBalance += amount;
    }

    public void addWinnings(int amount) {
        receiveChips(amount);
    }

    public void fold() {
        hasFolded = true;
        needsTurn = false;
    }

    public void setHasActed(boolean hasActed) {
        this.hasActed = hasActed;
    }

    public void setNeedsTurn(boolean needsTurn) {
        this.needsTurn = needsTurn && canAct();
    }

    public void finishTurn(int currentTableBet) {
        if (currentTableBet < 0) {
            throw new IllegalArgumentException(
                "Table bet cannot be negative.");
        }

        hasActed = true;
        needsTurn = false;
        lastBetFaced = currentTableBet;
    }

    public void startBettingRound() {
        currentBet = 0;
        hasActed = false;
        lastBetFaced = 0;
        needsTurn = canAct();
    }

    public void resetBet() {
        startBettingRound();
    }

    public void finishHand() {
        currentBet = 0;
        totalBet = 0;

        needsTurn = false;
        hasActed = false;
        lastBetFaced = 0;
    }
}