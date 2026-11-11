package poker;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class TurnQueue {
    private Queue<String> players;

    public TurnQueue() {
        players = new ArrayDeque<>();
    }


    public void enqueue(String player) {
        if (player == null || player.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name can't be blank.");
        }
        if (players.contains(player)) {
            throw new IllegalArgumentException(
                "That player is already in the queue.");
        }

        players.add(player);
    }


    public String peek() {
        if (players.isEmpty()) {
            throw new IllegalStateException("There are no players waiting.");
        }

        return players.element();
    }


    public String dequeue() {
        if (players.isEmpty()) {
            throw new IllegalStateException("There are no players waiting.");
        }

        return players.remove();
    }


    public boolean isEmpty() {
        return players.isEmpty();
    }


    public int size() {
        return players.size();
    }


    public List<String> getPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(players));
    }
}
