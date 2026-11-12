package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
    private List<Card> cards;
    private int nextCard;

    public Deck(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random cannot be null.");
        }

        cards = new ArrayList<Card>();
        nextCard = 0;

        for (Card.Suit suit : Card.Suit.values()) {
            for (int rank = 2; rank <= 14; rank++) {
                cards.add(new Card(rank, suit));
            }
        }

        Collections.shuffle(cards, random);
    }


    public Card deal() {
        if (nextCard >= cards.size()) {
            throw new IllegalStateException("The deck is empty.");
        }

        Card card = cards.get(nextCard);
        nextCard++;

        return card;
    }


    public int getRemaining() {
        return cards.size() - nextCard;
    }
}
