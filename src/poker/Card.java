package poker;

public class Card {
    public enum Suit {
        CLUBS,
        DIAMONDS,
        HEARTS,
        SPADES
    }

    private final int rank;
    private final Suit suit;

    public Card(int rank, Suit suit) {
        if (rank < 2 || rank > 14 || suit == null) {
            throw new IllegalArgumentException("Invalid card.");
        }

        this.rank = rank;
        this.suit = suit;
    }

    public int getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        String label;

        switch (rank) {
            case 11:
                label = "J";
                break;
            case 12:
                label = "Q";
                break;
            case 13:
                label = "K";
                break;
            case 14:
                label = "A";
                break;
            default:
                label = Integer.toString(rank);
                break;
        }

        return label + "-" + suit;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        Card card = (Card) other;
        return rank == card.rank && suit == card.suit;
    }

    @Override
    public int hashCode() {
        return 4 * rank + suit.ordinal();
    }
}