package poker;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class HandEvaluatorTest
{
    private List<Card> makeHand(Card... cards)
    {
        return new ArrayList<Card>(Arrays.asList(cards));
    }

    @Test
    public void testRoyalFlush()
    {
        List<Card> hand = makeHand(
            new Card(10, Card.Suit.HEARTS),
            new Card(11, Card.Suit.HEARTS),
            new Card(12, Card.Suit.HEARTS),
            new Card(13, Card.Suit.HEARTS),
            new Card(14, Card.Suit.HEARTS));

        HandEvaluator.HandValue value =
            HandEvaluator.evaluate(hand);

        assertEquals(9, value.getCategory());
        assertEquals("royal flush", value.getLabel());
    }

    @Test
    public void testOnePair()
    {
        List<Card> hand = makeHand(
            new Card(5, Card.Suit.CLUBS),
            new Card(5, Card.Suit.HEARTS),
            new Card(2, Card.Suit.SPADES),
            new Card(7, Card.Suit.DIAMONDS),
            new Card(9, Card.Suit.CLUBS));

        HandEvaluator.HandValue value =
            HandEvaluator.evaluate(hand);

        assertEquals(1, value.getCategory());
        assertEquals("one pair", value.getLabel());
    }

    @Test
    public void testStraight()
    {
        List<Card> hand = makeHand(
            new Card(2, Card.Suit.CLUBS),
            new Card(3, Card.Suit.HEARTS),
            new Card(4, Card.Suit.SPADES),
            new Card(5, Card.Suit.DIAMONDS),
            new Card(6, Card.Suit.CLUBS));

        HandEvaluator.HandValue value =
            HandEvaluator.evaluate(hand);

        assertEquals(4, value.getCategory());
        assertEquals("straight", value.getLabel());
    }

    @Test
    public void testHandComparison()
    {
        HandEvaluator.HandValue pair =
            new HandEvaluator.HandValue(1,
                Arrays.asList(10, 8, 7, 6));

        HandEvaluator.HandValue twoPair =
            new HandEvaluator.HandValue(2,
                Arrays.asList(10, 8, 7));

        assertTrue(twoPair.compareTo(pair) > 0);
        assertTrue(pair.compareTo(twoPair) < 0);
    }

    @Test
    public void testEqualHandValues()
    {
        HandEvaluator.HandValue hand1 =
            new HandEvaluator.HandValue(1,
                Arrays.asList(10, 8, 7, 6));

        HandEvaluator.HandValue hand2 =
            new HandEvaluator.HandValue(1,
                Arrays.asList(10, 8, 7, 6));

        assertEquals(0, hand1.compareTo(hand2));
    }

    @Test
    public void testNullHand()
    {
        try
        {
            HandEvaluator.evaluate(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }

    @Test
    public void testWrongHandSize()
    {
        List<Card> hand = makeHand(
            new Card(2, Card.Suit.CLUBS),
            new Card(3, Card.Suit.CLUBS));

        try
        {
            HandEvaluator.evaluate(hand);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }

    @Test
    public void testDuplicateCards()
    {
        Card card = new Card(2, Card.Suit.CLUBS);

        List<Card> hand = makeHand(
            card,
            card,
            new Card(4, Card.Suit.HEARTS),
            new Card(5, Card.Suit.HEARTS),
            new Card(6, Card.Suit.HEARTS));

        try
        {
            HandEvaluator.evaluate(hand);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }

    @Test
    public void testSevenCardHand()
    {
        List<Card> cards = makeHand(
            new Card(2, Card.Suit.CLUBS),
            new Card(3, Card.Suit.HEARTS),
            new Card(4, Card.Suit.SPADES),
            new Card(5, Card.Suit.DIAMONDS),
            new Card(6, Card.Suit.CLUBS),
            new Card(10, Card.Suit.HEARTS),
            new Card(12, Card.Suit.SPADES));

        HandEvaluator.HandValue value =
            HandEvaluator.evaluateBest(cards);

        assertNotNull(value);
        assertEquals(4, value.getCategory());
    }

    @Test
    public void testInvalidSevenCardHandSize()
    {
        List<Card> cards = makeHand(
            new Card(2, Card.Suit.CLUBS),
            new Card(3, Card.Suit.HEARTS),
            new Card(4, Card.Suit.SPADES));

        try
        {
            HandEvaluator.evaluateBest(cards);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }
}