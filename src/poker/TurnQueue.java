package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TurnQueue {
    private Queue<String> players;

    public TurnQueue() {
        players = new LinkedList<String>();
    }


    public void enqueue(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be blank.");
        }

        if (players.contains(name)) {
            throw new IllegalArgumentException(
                "That player is already in the queue.");
        }

        players.add(name);
    }


    public String peek() {
        if (players.isEmpty()) {
            throw new IllegalStateException("The turn queue is empty.");
        }

        return players.peek();
    }


    public String dequeue() {
        if (players.isEmpty()) {
            throw new IllegalStateException("The turn queue is empty.");
        }

        return players.remove();
    }


    public boolean isEmpty() {
        return players.isEmpty();
    }


    public int size() {
        return players.size();
    }


    public void clear() {
        players.clear();
    }


    public List<String> getPlayers() {
        List<String> copy = new ArrayList<String>(players);

        return Collections.unmodifiableList(copy);
    }
}
