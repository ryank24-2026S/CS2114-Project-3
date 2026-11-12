package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.NoSuchElementException;

public class Deck {

    private final Random random;
    private final List<Card> cards;
    private int currentIndex;

    public Deck(Random random) {
        if (random == null) {
            throw new IllegalArgumentException(
                "Random source cannot be null.");
        }

        this.random = random;
        this.cards = new ArrayList<>();

        shuffleNewDeck();
    }

    public void resetAndShuffle() {
        shuffleNewDeck();
    }

    private void shuffleNewDeck() {
        cards.clear();

        for (Card.Suit suit : Card.Suit.values()) {
            for (int rank = 2; rank <= 14; rank++) {
                cards.add(new Card(rank, suit));
            }
        }

        Collections.shuffle(cards, random);

        currentIndex = 0;
    }

    public Card deal() {
        if (currentIndex >= cards.size()) {
            throw new NoSuchElementException(
                "The deck is empty.");
        }

        Card card = cards.get(currentIndex);
        currentIndex++;

        return card;
    }

    public int remaining() {
        return cards.size() - currentIndex;
    }
}