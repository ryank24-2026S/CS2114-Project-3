package poker;

import java.util.List;

public class BettingRound {

    private int currentBet;
    private int minimumRaise;
    private int playersActed;
    private boolean roundComplete;

    public BettingRound(int minimumRaise) {
        if (minimumRaise <= 0) {
            throw new IllegalArgumentException(
                "Minimum raise must be positive.");
        }

        this.currentBet = 0;
        this.minimumRaise = minimumRaise;
        this.playersActed = 0;
        this.roundComplete = false;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public int getMinimumRaise() {
        return minimumRaise;
    }

    public int getPlayersActed() {
        return playersActed;
    }

    public boolean isRoundComplete() {
        return roundComplete;
    }

    public void setCurrentBet(int currentBet) {
        if (currentBet < 0) {
            throw new IllegalArgumentException(
                "Bet cannot be negative.");
        }

        this.currentBet = currentBet;
    }

    public void setMinimumRaise(int minimumRaise) {
        if (minimumRaise <= 0) {
            throw new IllegalArgumentException(
                "Minimum raise must be positive.");
        }

        this.minimumRaise = minimumRaise;
    }

    public void playerActed() {
        playersActed++;
    }

    public void resetPlayersActed() {
        playersActed = 0;
    }

    public void setRoundComplete(boolean complete) {
        roundComplete = complete;
    }

    public void recordRaise(int newBet) {
        if (newBet < currentBet + minimumRaise) {
            throw new IllegalArgumentException(
                "Raise must meet the minimum raise.");
        }

        minimumRaise = newBet - currentBet;
        currentBet = newBet;

        playersActed = 0;
        roundComplete = false;
    }

    public void reset() {
        currentBet = 0;
        playersActed = 0;
        roundComplete = false;
    }
}