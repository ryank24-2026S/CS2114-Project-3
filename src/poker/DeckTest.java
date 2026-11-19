package poker;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

public class DeckTest
{
    @Test
    public void testInitialDeckSize()
    {
        Deck deck = new Deck(new Random(1));

        assertEquals(52, deck.remaining());
    }

    @Test
    public void testDealCard()
    {
        Deck deck = new Deck(new Random(1));

        Card card = deck.deal();

        assertNotNull(card);
        assertEquals(51, deck.remaining());
    }

    @Test
    public void testDealEntireDeck()
    {
        Deck deck = new Deck(new Random(1));
        Set<Card> cards = new HashSet<Card>();

        for (int i = 0; i < 52; i++)
        {
            cards.add(deck.deal());
        }

        assertEquals(52, cards.size());
        assertEquals(0, deck.remaining());
    }

    @Test
    public void testResetAndShuffle()
    {
        Deck deck = new Deck(new Random(1));

        deck.deal();
        deck.deal();
        deck.deal();

        assertEquals(49, deck.remaining());

        deck.resetAndShuffle();

        assertEquals(52, deck.remaining());
    }

    @Test
    public void testDealEmptyDeck()
    {
        Deck deck = new Deck(new Random(1));

        for (int i = 0; i < 52; i++)
        {
            deck.deal();
        }

        try
        {
            deck.deal();
            fail("Expected NoSuchElementException");
        }
        catch (java.util.NoSuchElementException e)
        {
            assertEquals(0, deck.remaining());
        }
    }

    @Test
    public void testNullRandom()
    {
        try
        {
            new Deck(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }
}