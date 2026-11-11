package poker;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Deck {
    private Queue<Card> cards;

    public Deck(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random source cannot be null.");
        }

        List<Card> shuffled = new ArrayList<>();

        for (Card.Suit suit : Card.Suit.values()) {
            for (int rank = 2; rank <= 14; rank++) {
                shuffled.add(new Card(rank, suit));
            }
        }

        Collections.shuffle(shuffled, random);
        cards = new ArrayDeque<>(shuffled);
    }


    public Card deal() {
        if (cards.isEmpty()) {
            throw new IllegalArgumentException("The deck is empty.");
        }

        return cards.remove();
    }


    public int getRemaining() {
        return cards.size();
    }
}
