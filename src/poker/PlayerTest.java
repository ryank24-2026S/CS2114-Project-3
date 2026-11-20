package poker;

import static org.junit.Assert.*;

import org.junit.Test;

public class PlayerTest
{
    @Test
    public void testPlayerCreation()
    {
        Player player = new Player("Alice", 1000);

        assertEquals("Alice", player.getName());
        assertEquals(1000, player.getChips());
    }

    @Test
    public void testPay()
    {
        Player player = new Player("Alice", 1000);

        player.pay(100);

        assertEquals(900, player.getChips());
        assertEquals(100, player.getCurrentBet());
        assertEquals(100, player.getTotalBet());
    }

    @Test
    public void testReceiveChips()
    {
        Player player = new Player("Alice", 1000);

        player.receiveChips(500);

        assertEquals(1500, player.getChips());
    }

    @Test
    public void testFold()
    {
        Player player = new Player("Alice", 1000);

        player.startHand();
        player.fold();

        assertTrue(player.hasFolded());
    }

    @Test
    public void testStartHand()
    {
        Player player = new Player("Alice", 1000);

        player.startHand();

        assertTrue(player.isInHand());
        assertFalse(player.hasFolded());
        assertEquals(0, player.getCurrentBet());
        assertEquals(0, player.getTotalBet());
        assertEquals(0, player.getHoleCards().size());
    }

    @Test
    public void testAddCard()
    {
        Player player = new Player("Alice", 1000);

        player.startHand();

        Card card = new Card(10, Card.Suit.HEARTS);
        player.addCard(card);

        assertEquals(1, player.getHoleCards().size());
        assertEquals(card, player.getHoleCards().get(0));
    }

    @Test
    public void testAddNullCard()
    {
        Player player = new Player("Alice", 1000);

        player.startHand();

        try
        {
            player.addCard(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(0, player.getHoleCards().size());
        }
    }

    @Test
    public void testAddMoreThanTwoCards()
    {
        Player player = new Player("Alice", 1000);

        player.startHand();

        player.addCard(new Card(2, Card.Suit.CLUBS));
        player.addCard(new Card(3, Card.Suit.CLUBS));

        try
        {
            player.addCard(new Card(4, Card.Suit.CLUBS));
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(2, player.getHoleCards().size());
        }
    }

    @Test
    public void testPayMoreThanBalance()
    {
        Player player = new Player("Alice", 100);

        try
        {
            player.pay(200);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(100, player.getChips());
        }
    }

    @Test
    public void testNegativePayment()
    {
        Player player = new Player("Alice", 100);

        try
        {
            player.pay(-10);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(100, player.getChips());
        }
    }

    @Test
    public void testNegativeReceive()
    {
        Player player = new Player("Alice", 100);

        try
        {
            player.receiveChips(-10);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(100, player.getChips());
        }
    }
}