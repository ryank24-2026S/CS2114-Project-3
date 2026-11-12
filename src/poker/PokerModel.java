package poker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PokerModel {
    public enum State {
        LOBBY, PLAYING, HAND_FINISHED, GAME_OVER
    }


    public enum Stage {
        PREFLOP, FLOP, TURN, RIVER
    }

    private static final int MAX_PLAYERS = 6;
    private static final int STARTING_CHIPS = 100;
    private static final int MAX_STARTING_CHIPS = 10000;

    private static final int SMALL_BLIND = 1;
    private static final int BIG_BLIND = 2;

    private List<Player> players;
    private List<Card> communityCards;
    private List<String> results;

    private TurnQueue turns;

    private Random random;
    private Deck deck;

    private State state;
    private Stage stage;

    private int dealer;
    private int previousBigBlind;

    private int currentBet;
    private int minimumRaise;
    private int handNumber;

    public PokerModel(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random cannot be null.");
        }

        this.random = random;

        players = new ArrayList<Player>();
        communityCards = new ArrayList<Card>();
        results = new ArrayList<String>();

        turns = new TurnQueue();

        state = State.LOBBY;

        dealer = -1;
        previousBigBlind = -1;
    }


    public void addPlayer(String name) {
        addPlayer(name, STARTING_CHIPS);
    }


    public void addPlayer(String name, int chips) {
        if (state != State.LOBBY) {
            throw new IllegalStateException(
                "Players must join before the game starts.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }

        String cleanName = name.trim();

        if (cleanName.length() > 20 || !cleanName.matches("[A-Za-z0-9 _-]+")) {

            throw new IllegalArgumentException(
                "Use 1-20 letters, digits, spaces, _ or -.");
        }

        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(cleanName)) {

                throw new IllegalArgumentException(
                    "That name is already being used.");
            }
        }

        if (players.size() >= MAX_PLAYERS) {
            throw new IllegalArgumentException(
                "At most six players are allowed.");
        }

        if (chips < 1 || chips > MAX_STARTING_CHIPS) {
            throw new IllegalArgumentException(
                "Starting chips must be between 1 and 10000.");
        }

        players.add(new Player(cleanName, chips));
    }


    public void startRound() {
        if (state != State.LOBBY && state != State.HAND_FINISHED) {

            throw new IllegalStateException("Cannot start a hand now.");
        }

        int fundedPlayers = countFundedPlayers();

        if (fundedPlayers < 2) {
            throw new IllegalStateException("At least two players need chips.");
        }

        if (fundedPlayers == 2 && previousBigBlind >= 0) {

            int nextBigBlind = nextFundedPlayer(previousBigBlind);

            dealer = nextFundedPlayer(nextBigBlind);
        }
        else {
            dealer = nextFundedPlayer(dealer);
        }

        for (Player player : players) {
            player.startHand();
        }

        communityCards.clear();
        results.clear();
        turns.clear();

        deck = new Deck(random);

        int smallBlindPlayer;

        if (fundedPlayers == 2) {
            smallBlindPlayer = dealer;
        }
        else {
            smallBlindPlayer = nextFundedPlayer(dealer);
        }

        int bigBlindPlayer = nextFundedPlayer(smallBlindPlayer);

        previousBigBlind = bigBlindPlayer;

        dealHoleCards();

        postBlind(players.get(smallBlindPlayer), SMALL_BLIND);
        postBlind(players.get(bigBlindPlayer), BIG_BLIND);

        currentBet = BIG_BLIND;
        minimumRaise = BIG_BLIND;

        stage = Stage.PREFLOP;
        state = State.PLAYING;
        handNumber++;

        for (Player player : players) {
            player.setNeedsTurn(player.canAct());
        }

        advanceGame(bigBlindPlayer);
    }


    public void startNextHand() {
        if (state != State.HAND_FINISHED) {
            throw new IllegalStateException("Finish the current hand first.");
        }

        startRound();
    }


    private int countFundedPlayers() {
        int count = 0;

        for (Player player : players) {
            if (player.getChips() > 0) {
                count++;
            }
        }

        return count;
    }


    private int nextFundedPlayer(int after) {
        int index = after;

        for (int i = 0; i < players.size(); i++) {
            index++;

            if (index >= players.size()) {
                index = 0;
            }

            if (players.get(index).getChips() > 0) {
                return index;
            }
        }

        throw new IllegalStateException("No player has chips.");
    }


    private int findPlayerIndex(String name) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(name)) {
                return i;
            }
        }

        throw new IllegalArgumentException("Unknown player.");
    }


    private void dealHoleCards() {
        for (int card = 0; card < 2; card++) {
            int index = dealer;

            for (int i = 0; i < players.size(); i++) {
                index++;

                if (index >= players.size()) {
                    index = 0;
                }

                Player player = players.get(index);

                if (player.isInHand()) {
                    player.addCard(deck.deal());
                }
            }
        }
    }


    private void postBlind(Player player, int amount) {
        if (amount > player.getChips()) {
            amount = player.getChips();
        }

        player.pay(amount);
    }


    private Player requireTurn() {
        if (state != State.PLAYING || turns.isEmpty()) {

            throw new IllegalStateException("No turn is active.");
        }

        return getCurrentPlayer();
    }


    private int amountOwed(Player player) {
        int amount = currentBet - player.getRoundBet();

        if (amount < 0) {
            amount = 0;
        }

        return amount;
    }


    private void completeAction(Player player) {
        int index = findPlayerIndex(player.getName());

        player.finishTurn(currentBet);

        turns.dequeue();

        advanceGame(index);
    }


    public void check() {
        Player player = requireTurn();

        if (amountOwed(player) > 0) {
            throw new IllegalArgumentException(
                "You must call, raise, or fold.");
        }

        completeAction(player);
    }


    public void call() {
        Player player = requireTurn();

        int amount = amountOwed(player);

        if (amount == 0) {
            throw new IllegalArgumentException("Nothing to call. Use check.");
        }

        if (amount > player.getChips()) {
            amount = player.getChips();
        }

        player.pay(amount);

        completeAction(player);
    }


    public void fold() {
        Player player = requireTurn();

        player.fold();

        completeAction(player);
    }


    public void bet(int amount) {
        requireTurn();

        if (currentBet != 0) {
            throw new IllegalArgumentException(
                "A bet already exists. Use raise.");
        }

        increaseBet(amount);
    }


    public void raiseTo(int total) {
        requireTurn();

        if (currentBet == 0) {
            throw new IllegalArgumentException("No bet exists. Use bet.");
        }

        increaseBet(total);
    }


    public void allIn() {
        Player player = requireTurn();

        int total = player.getRoundBet() + player.getChips();

        if (total <= currentBet) {
            player.pay(player.getChips());

            completeAction(player);
        }
        else {
            increaseBet(total);
        }
    }


    private boolean hasRaiseRight(Player player) {
        if (!player.hasActed()) {
            return true;
        }

        if (player.getLastBetFaced() == 0) {
            return true;
        }

        int increase = currentBet - player.getLastBetFaced();

        return increase >= minimumRaise;
    }


    private boolean anotherPlayerCanAct(Player player) {
        for (Player other : players) {
            if (other != player && other.canAct()) {
                return true;
            }
        }

        return false;
    }


    private void increaseBet(int total) {
        Player player = requireTurn();

        int maximum = player.getRoundBet() + player.getChips();

        if (total <= currentBet || total > maximum) {
            throw new IllegalArgumentException(
                "The total must exceed the current bet "
                    + "and fit your stack.");
        }

        if (!anotherPlayerCanAct(player)) {
            throw new IllegalArgumentException(
                "No opponent can call another bet.");
        }

        if (!hasRaiseRight(player)) {
            throw new IllegalArgumentException(
                "Betting has not reopened. Call or fold.");
        }

        int increase = total - currentBet;

        boolean fullRaise = increase >= minimumRaise;

        boolean allIn = total == maximum;

        if (!fullRaise && !allIn) {
            throw new IllegalArgumentException("Minimum total is " + (currentBet
                + minimumRaise) + ". A smaller increase must be all-in.");
        }

        int payment = total - player.getRoundBet();

        player.pay(payment);

        currentBet = total;

        if (fullRaise) {
            minimumRaise = increase;
        }

        for (Player other : players) {
            if (other != player && other.canAct() && other
                .getRoundBet() < currentBet) {

                other.setNeedsTurn(true);
            }
        }

        completeAction(player);
    }


    private void buildTurnQueue(int after) {
        turns.clear();

        int index = after;

        for (int i = 0; i < players.size(); i++) {
            index++;

            if (index >= players.size()) {
                index = 0;
            }

            Player player = players.get(index);

            if (player.canAct() && player.needsTurn()) {

                turns.enqueue(player.getName());
            }
        }
    }


    private void advanceGame(int after) {
        while (state == State.PLAYING) {
            if (countActivePlayers() == 1) {
                awardLastPlayer();
                return;
            }

            if (countPlayersWhoCanAct() <= 1) {
                for (Player player : players) {
                    if (player.canAct()) {
                        player.setNeedsTurn(amountOwed(player) > 0);
                    }
                }
            }

            buildTurnQueue(after);

            if (!turns.isEmpty()) {
                return;
            }

            returnUncalledChips();

            if (stage == Stage.RIVER) {
                awardShowdownPots();
                finishHand();
                return;
            }

            dealNextStreet();

            currentBet = 0;
            minimumRaise = BIG_BLIND;

            for (Player player : players) {
                player.startBettingRound();
            }

            after = dealer;
        }
    }


    private int countActivePlayers() {
        int count = 0;

        for (Player player : players) {
            if (player.isActive()) {
                count++;
            }
        }

        return count;
    }


    private int countPlayersWhoCanAct() {
        int count = 0;

        for (Player player : players) {
            if (player.canAct()) {
                count++;
            }
        }

        return count;
    }


    private void dealNextStreet() {
        deck.deal(); // Burn one card.

        if (stage == Stage.PREFLOP) {
            communityCards.add(deck.deal());
            communityCards.add(deck.deal());
            communityCards.add(deck.deal());

            stage = Stage.FLOP;
        }
        else if (stage == Stage.FLOP) {
            communityCards.add(deck.deal());

            stage = Stage.TURN;
        }
        else if (stage == Stage.TURN) {
            communityCards.add(deck.deal());

            stage = Stage.RIVER;
        }
    }


    private void returnUncalledChips() {
        Player highestPlayer = null;

        int highest = 0;
        int secondHighest = 0;

        for (Player player : players) {
            int amount = player.getTotalBet();

            if (amount > highest) {
                secondHighest = highest;
                highest = amount;
                highestPlayer = player;
            }
            else if (amount > secondHighest) {
                secondHighest = amount;
            }
        }

        if (highestPlayer != null && highest > secondHighest) {

            int refund = highest - secondHighest;

            highestPlayer.refund(refund);

            results.add(highestPlayer.getName() + " receives " + refund
                + " uncalled chips back.");
        }
    }


    private void awardLastPlayer() {
        returnUncalledChips();

        int pot = getPot();

        for (Player player : players) {
            if (player.isActive()) {
                player.receiveChips(pot);

                results.add(player.getName() + " wins " + pot
                    + " without showdown.");
            }
        }

        finishHand();
    }


    private int nextPotLevel(int previousLevel) {
        int nextLevel = 0;

        for (Player player : players) {
            int amount = player.getTotalBet();

            if (amount > previousLevel) {
                if (nextLevel == 0 || amount < nextLevel) {
                    nextLevel = amount;
                }
            }
        }

        return nextLevel;
    }


    private void awardShowdownPots() {
        int previousLevel = 0;
        int level = nextPotLevel(previousLevel);
        int potNumber = 1;

        while (level != 0) {
            int contributors = 0;

            List<Player> eligible = new ArrayList<Player>();

            for (Player player : players) {
                if (player.getTotalBet() >= level) {
                    contributors++;

                    if (player.isActive()) {
                        eligible.add(player);
                    }
                }
            }

            int amount = (level - previousLevel) * contributors;

            List<Player> winners = findBestPlayers(eligible);

            if (winners.isEmpty()) {
                throw new IllegalStateException(
                    "A pot has no eligible winner.");
            }

            awardPot(amount, winners, potNumber);

            previousLevel = level;
            level = nextPotLevel(previousLevel);

            potNumber++;
        }
    }


    private List<Player> findBestPlayers(List<Player> eligible) {

        List<Player> winners = new ArrayList<Player>();

        HandEvaluator.HandValue best = null;

        for (Player player : eligible) {
            HandEvaluator.HandValue value = getHandValue(player);

            if (best == null) {
                best = value;
                winners.add(player);
            }
            else {
                int comparison = value.compareTo(best);

                if (comparison > 0) {
                    best = value;

                    winners.clear();
                    winners.add(player);
                }
                else if (comparison == 0) {
                    winners.add(player);
                }
            }
        }

        return winners;
    }


    private void awardPot(int amount, List<Player> winners, int potNumber) {

        int share = amount / winners.size();
        int extra = amount % winners.size();

        String names = "";

        int index = dealer;

        for (int i = 0; i < players.size(); i++) {
            index++;

            if (index >= players.size()) {
                index = 0;
            }

            Player player = players.get(index);

            if (winners.contains(player)) {
                int payment = share;

                if (extra > 0) {
                    payment++;
                    extra--;
                }

                player.receiveChips(payment);

                if (!names.isEmpty()) {
                    names += ", ";
                }

                names += player.getName();
            }
        }

        results.add("Pot " + potNumber + ": " + amount + " chips -> " + names);
    }


    private void finishHand() {
        for (Player player : players) {
            player.finishHand();
        }

        turns.clear();
        currentBet = 0;

        if (countFundedPlayers() > 1) {
            state = State.HAND_FINISHED;
        }
        else {
            state = State.GAME_OVER;
        }
    }


    public HandEvaluator.HandValue getHandValue(Player player) {

        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        if (communityCards.size() != 5 || player.getCards().size() != 2) {

            throw new IllegalStateException(
                "Two hole cards and five community cards " + "are required.");
        }

        List<Card> cards = player.getCards();

        cards.addAll(communityCards);

        return HandEvaluator.evaluateBest(cards);
    }


    public State getState() {
        return state;
    }


    public Stage getStage() {
        return stage;
    }


    public int getHandNumber() {
        return handNumber;
    }


    public int getCurrentBet() {
        return currentBet;
    }


    public int getMinimumRaiseTo() {
        return currentBet + minimumRaise;
    }


    public int getAmountToCall() {
        Player player = requireTurn();

        return amountOwed(player);
    }


    public Player getCurrentPlayer() {
        if (state != State.PLAYING || turns.isEmpty()) {

            return null;
        }

        String name = turns.peek();

        int index = findPlayerIndex(name);

        return players.get(index);
    }


    public String getDealerName() {
        if (dealer == -1) {
            return "None";
        }

        return players.get(dealer).getName();
    }


    public int getPot() {
        int total = 0;

        for (Player player : players) {
            total += player.getTotalBet();
        }

        return total;
    }


    public List<Player> getPlayers() {
        return new ArrayList<Player>(players);
    }


    public List<Card> getCommunityCards() {
        return new ArrayList<Card>(communityCards);
    }


    public List<String> getResults() {
        return new ArrayList<String>(results);
    }


    public List<String> getPendingTurns() {
        return turns.getPlayers();
    }
}
