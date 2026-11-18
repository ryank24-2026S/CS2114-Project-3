package poker;
import java.util.List;
public class CardRenderer {
   private static final int CARD_HEIGHT = 7;
   private CardRenderer() {
   }
   public static String render(List<Card> cards) {
       if (cards == null) {
           throw new IllegalArgumentException("Cards cannot be null");
       }
       if (cards.isEmpty()) {
           return "(no cards)";
       }
       return render(cards, cards.size());
   }
   public static String render(List<Card> cards, int slots) {
       if (cards == null) {
           throw new IllegalArgumentException("Cards cannot be null");
       }
       if (slots < cards.size() || slots < 0) {
           throw new IllegalArgumentException(
               "Slots cannot be fewer than the visible cards.");
       }
       for (Card card : cards) {
           if (card == null) {
               throw new IllegalArgumentException(
                   "A visible card cannot be null");
           }
       }
       if (slots == 0) {
           return "(no cards)";
       }
       StringBuilder output = new StringBuilder();
       for (int row = 0; row < CARD_HEIGHT; row++) {
           for (int index = 0; index < slots; index++) {
               if (index > 0) {
                   output.append(" ");
               }
               Card card = index < cards.size() ? cards.get(index) : null;
               output.append(getLine(card, row));
           }
           if (row < CARD_HEIGHT - 1) {
               output.append('\n');
           }
       }
       return output.toString();
   }
   private static String getLine(Card card, int row) {
       if (row == 0 || row == CARD_HEIGHT - 1) {
           return "+-------+";
       }
       if (card == null) {
           return "|///////|";
       }
       String rank = getRankLabel(card.getRank());
       switch (row) {
           case 1:
               return String.format("| %-2s    |", rank);
           case 3:
               return "|   " + getSuitLabel(card.getSuit()) + "   |";
           case 5:
               return String.format("|    %2s |", rank);
           default:
               return "|       |";
       }
   }
   private static String getRankLabel(int rank) {
       switch (rank) {
           case 11:
               return "J";
           case 12:
               return "Q";
           case 13:
               return "K";
           case 14:
               return "A";
           default:
               return Integer.toString(rank);
       }
   }
   private static String getSuitLabel(Card.Suit suit) {
       switch (suit) {
           case CLUBS:
               return "C";
           case DIAMONDS:
               return "D";
           case HEARTS:
               return "H";
           case SPADES:
               return "S";
           default:
               throw new IllegalArgumentException("Unknown suit.");
       }
   }
}