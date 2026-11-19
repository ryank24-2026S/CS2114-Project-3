package poker;

import static org.junit.Assert.*;

import org.junit.Test;

public class CardTest
{
    @Test
    public void testValidCard()
    {
        Card card = new Card(14, Card.Suit.HEARTS);

        assertEquals(14, card.getRank());
        assertEquals(Card.Suit.HEARTS, card.getSuit());
    }

    @Test
    public void testCardEquality()
    {
        Card card1 = new Card(10, Card.Suit.CLUBS);
        Card card2 = new Card(10, Card.Suit.CLUBS);

        assertEquals(card1, card2);
        assertEquals(card1.hashCode(), card2.hashCode());
    }

    @Test
    public void testDifferentCards()
    {
        Card card1 = new Card(10, Card.Suit.CLUBS);
        Card card2 = new Card(10, Card.Suit.HEARTS);

        assertNotEquals(card1, card2);
    }

    @Test
    public void testInvalidLowRank()
    {
        try
        {
            new Card(1, Card.Suit.CLUBS);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e);
        }
    }

    @Test
    public void testInvalidHighRank()
    {
        try
        {
            new Card(15, Card.Suit.CLUBS);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e);
        }
    }

    @Test
    public void testNullSuit()
    {
        try
        {
            new Card(5, null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertNotNull(e);
        }
    }

    @Test
    public void testCardToString()
    {
        Card card = new Card(14, Card.Suit.SPADES);

        assertEquals("A-SPADES", card.toString());
    }
}