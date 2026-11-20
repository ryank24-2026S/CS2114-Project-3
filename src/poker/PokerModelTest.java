package poker;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

public class PokerModelTest
{
    @Test
    public void testModelCreation()
    {
        PokerModel model = new PokerModel(new Random(1));

        assertEquals(PokerModel.State.LOBBY, model.getState());
        assertEquals(0, model.getPlayers().size());
        assertEquals(0, model.getHandNumber());
    }

    @Test
    public void testAddPlayer()
    {
        PokerModel model = new PokerModel(new Random(1));

        model.addPlayer("Alice");

        assertEquals(1, model.getPlayers().size());
        assertEquals("Alice", model.getPlayers().get(0).getName());
        assertEquals(100, model.getPlayers().get(0).getChips());
    }

    @Test
    public void testAddPlayerWithCustomChips()
    {
        PokerModel model = new PokerModel(new Random(1));

        model.addPlayer("Alice", 500);

        assertEquals(500, model.getPlayers().get(0).getChips());
    }

    @Test
    public void testAddMultiplePlayers()
    {
        PokerModel model = new PokerModel(new Random(1));

        model.addPlayer("Alice");
        model.addPlayer("Bob");
        model.addPlayer("Charlie");

        assertEquals(3, model.getPlayers().size());
    }

    @Test
    public void testBlankPlayerName()
    {
        PokerModel model = new PokerModel(new Random(1));

        try
        {
            model.addPlayer(" ");
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(0, model.getPlayers().size());
        }
    }

    @Test
    public void testNullPlayerName()
    {
        PokerModel model = new PokerModel(new Random(1));

        try
        {
            model.addPlayer(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(0, model.getPlayers().size());
        }
    }

    @Test
    public void testDuplicatePlayerName()
    {
        PokerModel model = new PokerModel(new Random(1));

        model.addPlayer("Alice");

        try
        {
            model.addPlayer("alice");
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(1, model.getPlayers().size());
        }
    }

    @Test
    public void testInvalidChipAmount()
    {
        PokerModel model = new PokerModel(new Random(1));

        try
        {
            model.addPlayer("Alice", 0);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(0, model.getPlayers().size());
        }
    }

    @Test
    public void testTooManyPlayers()
    {
        PokerModel model = new PokerModel(new Random(1));

        model.addPlayer("Alice");
        model.addPlayer("Bob");
        model.addPlayer("Charlie");
        model.addPlayer("David");
        model.addPlayer("Eve");
        model.addPlayer("Frank");

        try
        {
            model.addPlayer("Grace");
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(6, model.getPlayers().size());
        }
    }

    @Test
    public void testNullRandom()
    {
        try
        {
            new PokerModel(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }

    @Test
    public void testStartRoundWithInsufficientPlayers()
    {
        PokerModel model = new PokerModel(new Random(1));

        model.addPlayer("Alice");

        try
        {
            model.startRound();
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e)
        {
            assertEquals(PokerModel.State.LOBBY, model.getState());
        }
    }
}