package poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HandEvaluator {
    private HandEvaluator() {
    }


    public static HandValue evaluate(List<Card> hand) {
        validateHand(hand);

        int counts[] = new int[15];
        List<Integer> descending = new ArrayList<>();

        Card.Suit firstSuit = hand.get(0).getSuit();
        boolean flush = true;

        for (Card card : hand) {
            counts[card.getRank()]++;
            descending.add(card.getRank());

            if (card.getSuit() != firstSuit) {
                flush = false;
            }
        }

        Collections.sort(descending, Collections.reverseOrder());

        int straightHigh = getStraightHigh(counts);

        List<Integer> groups = getRankGroups(counts);

        int firstCount = counts[groups.get(0)];

        if (flush && straightHigh > 0) {
            return new HandValue(8, Collections.singletonList(straightHigh));
        }
        if (firstCount == 4) {
            return new HandValue(7, groups);
        }
        if (groups.size() == 2 && firstCount == 3) {
            return new HandValue(6, groups);
        }
        if (flush) {
            return new HandValue(5, descending);
        }
        if (straightHigh > 0) {
            return new HandValue(4, Collections.singletonList(straightHigh));
        }
        if (firstCount == 3) {
            return new HandValue(3, groups);
        }
        if (groups.size() == 3 && firstCount == 2 && counts[groups.get(
            1)] == 2) {
            return new HandValue(2, groups);
        }
        if (firstCount == 2) {
            return new HandValue(1, groups);
        }

        return new HandValue(0, descending);
    }


    private static void validateHand(List<Card> hand) {
        if (hand == null || hand.size() != 5) {
            throw new IllegalArgumentException(
                "A hand must contain exactly five cards.");
        }

        Set<Card> checked = new HashSet<>();

        for (Card card : hand) {
            if (card == null || !checked.add(card)) {
                throw new IllegalArgumentException(
                    "A hand must contain five distinct, non-null cards.");
            }
        }
    }


    private static int getStraightHigh(int[] counts) {
        int consecutive = 0;

        for (int rank = 14; rank >= 2; rank--) {
            if (counts[rank] > 0) {
                consecutive++;

                if (consecutive == 5) {
                    return rank + 4;
                }
            }
            else {
                consecutive = 0;
            }
        }

        if (counts[14] > 0 && counts[5] > 0 && counts[4] > 0 && counts[3] > 0
            && counts[2] > 0) {
            return 5;
        }

        return 0;
    }


    private static List<Integer> getRankGroups(int[] counts) {
        List<Integer> groups = new ArrayList<>();

        for (int groupSize = 4; groupSize >= 1; groupSize--) {
            for (int rank = 14; rank >= 2; rank--) {
                if (counts[rank] == groupSize) {
                    groups.add(rank);
                }
            }
        }

        return groups;
    }

    // ---------------------------------------------------------------------
    /**
     * Stores a hand's category and ordered tiebreaking ranks.
     */
    public static class HandValue implements Comparable<HandValue> {
        private final int category;
        private final List<Integer> tieBreak;

        public HandValue(int category, List<Integer> tieBreak) {
            if (category < 0 || category > 8) {
                throw new IllegalArgumentException(
                    "Hand category must be between 0 and 8.");
            }
            if (tieBreak == null || tieBreak.isEmpty()) {
                throw new IllegalArgumentException(
                    "Tiebreaking ranks are required.");
            }

            for (Integer rank : tieBreak) {
                if (rank == null || rank < 2 || rank > 14) {
                    throw new IllegalArgumentException(
                        "Invalid tiebreaking rank.");
                }
            }

            this.category = category;
            this.tieBreak = Collections.unmodifiableList(new ArrayList<>(
                tieBreak));
        }


        public int getCategory() {
            return category;
        }


        public List<Integer> getTieBreak() {
            return tieBreak;
        }


        public String getLabel() {
            switch (category) {
                case 8:
                    return "straight flush";
                case 7:
                    return "four of a kind";
                case 6:
                    return "full house";
                case 5:
                    return "flush";
                case 4:
                    return "straight";
                case 3:
                    return "three of a kind";
                case 2:
                    return "two pair";
                case 1:
                    return "one pair";
                default:
                    return "high card";
            }
        }


        @Override
        public int compareTo(HandValue other) {
            int result = Integer.compare(category, other.category);

            if (result != 0) {
                return result;
            }

            int limit = Math.min(tieBreak.size(), other.tieBreak.size());

            for (int i = 0; i < limit; i++) {
                result = Integer.compare(tieBreak.get(i), other.tieBreak.get(
                    i));

                if (result != 0) {
                    return result;
                }
            }

            return Integer.compare(tieBreak.size(), other.tieBreak.size());
        }
    }

    public static HandValue evaluateBest(List<Card> cards) {
        if (cards == null || cards.size() != 7) {
            throw new IllegalArgumentException(
                "Exactly seven cards are required.");
        }

        Set<Card> checked = new HashSet<>();

        for (Card card : cards) {
            if (card == null || !checked.add(card)) {
                throw new IllegalArgumentException(
                    "Cards must be distinct and non-null.");
            }
        }

        HandValue best = null;

        for (int a = 0; a < 3; a++) {
            for (int b = a + 1; b < 4; b++) {
                for (int c = b + 1; c < 5; c++) {
                    for (int d = c + 1; d < 6; d++) {
                        for (int e = d + 1; e < 7; e++) {
                            List<Card> candidate = new ArrayList<>();

                            candidate.add(cards.get(a));
                            candidate.add(cards.get(b));
                            candidate.add(cards.get(c));
                            candidate.add(cards.get(d));
                            candidate.add(cards.get(e));

                            HandValue value = evaluate(candidate);

                            if (best == null || value.compareTo(best) > 0) {
                                best = value;
                            }
                        }
                    }
                }
            }
        }

        return best;
    }
}
