package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class PokerModel {
    public enum State {
        LOBBY, PLAYING, FINISHED
    }
    
    public enum Stage{
        PREFLOP,
        FLOP,
        TURN,
        RIVER
    }

    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;
    private static final int CARDS_PER_HAND = 2;

    private List<String> players;
    private Map<String, List<Card>> hands;
    private Set<String> activePlayers;
    private TurnQueue turns;
    private Random random;
    private State state;
    private List<String> winners;
    private Deck deck;
    private List<Card> communityCards;
    private Stage stage;

    public PokerModel(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random source can't be null.");
        }

        this.random = random;
        players = new ArrayList<>();
        hands = new LinkedHashMap<>();
        activePlayers = new LinkedHashSet<>();
        turns = new TurnQueue();
        state = State.LOBBY;
        winners = new ArrayList<>();
        communityCards = new ArrayList<>();
    }


    public void addPlayer(String name) {
        requireState(State.LOBBY);

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name can't be blank.");
        }

        String cleanName = name.trim();

        if (cleanName.length() > 20 || !cleanName.matches("[A-Za-z0-9 _-]+")) {
            throw new IllegalArgumentException(
                "Names must use 1-20 letters, digits, spaces, "
                    + "underscores, or hyphens.");
        }

        for (String player : players) {
            if (player.equalsIgnoreCase(cleanName)) {
                throw new IllegalArgumentException(
                    "That player name is already being used.");
            }
        }

        if (players.size() >= MAX_PLAYERS) {
            throw new IllegalArgumentException(
                "At most six players are allowed");
        }

        players.add(cleanName);
    }


    public void startRound() {
        requireState(State.LOBBY);

        if (players.size() < MIN_PLAYERS) {
            throw new IllegalStateException(
                "Add at least two players before starting.");
        }

        deck = new Deck(random);

        for (String player : players) {
            hands.put(player, new ArrayList<>());
            activePlayers.add(player);
            turns.enqueue(player);
        }

        for (int i = 0; i < CARDS_PER_HAND; i++) {
            for (String player : players) {
                hands.get(player).add(deck.deal());
            }
        }

        stage = Stage.PREFLOP;
        state = State.PLAYING;
    }


    public void check(String player) {
        requireTurn(player);

        turns.dequeue();
        finishIfReady();
    }


    public void fold(String player) {
        requireTurn(player);

        turns.dequeue();
        activePlayers.remove(player);

        finishIfReady();
    }


    private void requireTurn(String player) {
        requireState(State.PLAYING);

        if (!turns.peek().equals(player)) {
            throw new IllegalArgumentException("It is not that player's turn.");
        }
    }


    private void requireState(State requiredState) {
        if (state != requiredState) {
            throw new IllegalArgumentException("This action requires state "
                + requiredState + ".");
        }
    }


    private void finishIfReady() {
        if (activePlayers.size() == 1) {
            winners = new ArrayList<>(activePlayers);
            state = State.FINISHED;
        }
        else if (turns.isEmpty()) {
            findShowdownWinners();
            state = State.FINISHED;
        }
        else {
            advanceStage();
            createStageTurns();
        }
    }

    
    public HandEvaluator.HandValue getHandValue(String player){
        if(communityCards.size() != 5) {
            throw new IllegalStateException("Five community cards are required to evaluate a hand.");
        }
        
        List<Card> availableCards = new ArrayList<>(getHand(player));
        availableCards.addAll(communityCards);
        return HandEvaluator.evaluateBest(availableCards);
    }

    private void findShowdownWinners() {
        HandEvaluator.HandValue bestHand = null;
        List<String> roundWinners = new ArrayList<>();

        for (String player : activePlayers) {
            HandEvaluator.HandValue handValue = getHandValue(player);

            if (bestHand == null || handValue.compareTo(bestHand) > 0) {
                bestHand = handValue;
                roundWinners.clear();
                roundWinners.add(player);
            }
            else if (handValue.compareTo(bestHand) == 0) {
                roundWinners.add(player);
            }
        }

        winners = roundWinners;
    }


    public State getState() {
        return state;
    }


    public List<String> getPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(players));
    }


    public List<String> getActivePlayers() {
        return Collections.unmodifiableList(new ArrayList<>(activePlayers));
    }


    public List<String> getPendingTurns() {
        if (state != State.PLAYING) {
            return Collections.emptyList();
        }

        return turns.getPlayers();
    }


    public String getCurrentPlayer() {
        if (state != State.PLAYING) {
            return null;
        }

        return turns.peek();
    }


    public List<Card> getHand(String player) {
        if (!hands.containsKey(player)) {
            throw new IllegalArgumentException(
                "That player does not have a dealt hand.");
        }

        return Collections.unmodifiableList(new ArrayList<>(hands.get(player)));
    }


    public List<String> getWinners() {
        return Collections.unmodifiableList(new ArrayList<>(winners));
    }
    
    public List<Card> getCommunityCards(){
        return Collections.unmodifiableList(new ArrayList<>(communityCards));
    }
    
    public Stage getStage() {
        return stage;
    }
    
    private void advanceStage() {
        deck.deal();
        
        switch(stage){
            case PREFLOP:
                communityCards.add(deck.deal());
                communityCards.add(deck.deal());
                communityCards.add(deck.deal());
                stage = Stage.FLOP;
                break;
            case FLOP:
                communityCards.add(deck.deal());
                stage=Stage.TURN;
                break;
            case TURN:
                communityCards.add(deck.deal());
                stage=Stage.RIVER;
                break;
            default:
                throw new IllegalStateException("There are no more community cards to deal.");
        }
    }
    
    private void createStageTurns() {
        turns = new TurnQueue();
        
        for(String player : activePlayers) {
            turns.enqueue(player);
        }
    }
}
