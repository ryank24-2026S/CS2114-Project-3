package poker;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int chips;

    private List<Card> cards;

    private boolean inHand;
    private boolean folded;
    private boolean needsTurn;
    private boolean acted;

    private int roundBet;
    private int totalBet;
    private int lastBetFaced;

    public Player(String name, int chips) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }

        if (chips < 0) {
            throw new IllegalArgumentException("Chips cannot be negative.");
        }

        this.name = name;
        this.chips = chips;

        cards = new ArrayList<Card>();
    }


    public void startHand() {
        cards.clear();

        inHand = chips > 0;
        folded = false;
        needsTurn = false;
        acted = false;

        roundBet = 0;
        totalBet = 0;
        lastBetFaced = 0;
    }


    public void startBettingRound() {
        roundBet = 0;
        lastBetFaced = 0;
        acted = false;

        needsTurn = canAct();
    }


    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null.");
        }

        cards.add(card);
    }


    public void pay(int amount) {
        if (amount < 0 || amount > chips) {
            throw new IllegalArgumentException("Invalid payment.");
        }

        chips -= amount;
        roundBet += amount;
        totalBet += amount;
    }


    public void receiveChips(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                "Cannot receive negative chips.");
        }

        chips += amount;
    }


    public void refund(int amount) {
        if (amount < 0 || amount > totalBet) {
            throw new IllegalArgumentException("Invalid refund.");
        }

        chips += amount;
        totalBet -= amount;
        roundBet -= amount;

        if (roundBet < 0) {
            roundBet = 0;
        }
    }


    public void fold() {
        folded = true;
        needsTurn = false;
    }


    public void finishTurn(int currentBet) {
        needsTurn = false;
        acted = true;
        lastBetFaced = currentBet;
    }


    public void finishHand() {
        roundBet = 0;
        totalBet = 0;
        needsTurn = false;
    }


    public boolean isActive() {
        return inHand && !folded;
    }


    public boolean canAct() {
        return isActive() && chips > 0;
    }


    public String getName() {
        return name;
    }


    public int getChips() {
        return chips;
    }


    public List<Card> getCards() {
        return new ArrayList<Card>(cards);
    }


    public boolean isInHand() {
        return inHand;
    }


    public boolean isFolded() {
        return folded;
    }


    public boolean needsTurn() {
        return needsTurn;
    }


    public void setNeedsTurn(boolean value) {
        needsTurn = value;
    }


    public boolean hasActed() {
        return acted;
    }


    public int getRoundBet() {
        return roundBet;
    }


    public int getTotalBet() {
        return totalBet;
    }


    public int getLastBetFaced() {
        return lastBetFaced;
    }
}
