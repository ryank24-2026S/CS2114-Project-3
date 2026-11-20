package poker;

import static org.junit.Assert.*;

import org.junit.Test;

public class BettingRoundTest
{
    @Test
    public void testInitialValues()
    {
        BettingRound round = new BettingRound(2);

        assertEquals(0, round.getCurrentBet());
        assertEquals(2, round.getMinimumRaise());
        assertEquals(0, round.getPlayersActed());
        assertFalse(round.isRoundComplete());
    }

    @Test
    public void testSetCurrentBet()
    {
        BettingRound round = new BettingRound(2);

        round.setCurrentBet(10);

        assertEquals(10, round.getCurrentBet());
    }

    @Test
    public void testRecordRaise()
    {
        BettingRound round = new BettingRound(2);

        round.setCurrentBet(10);
        round.recordRaise(15);

        assertEquals(15, round.getCurrentBet());
        assertEquals(5, round.getMinimumRaise());
        assertEquals(0, round.getPlayersActed());
    }

    @Test
    public void testPlayerActed()
    {
        BettingRound round = new BettingRound(2);

        round.playerActed();
        round.playerActed();

        assertEquals(2, round.getPlayersActed());
    }

    @Test
    public void testResetPlayersActed()
    {
        BettingRound round = new BettingRound(2);

        round.playerActed();
        round.playerActed();

        round.resetPlayersActed();

        assertEquals(0, round.getPlayersActed());
    }

    @Test
    public void testSetRoundComplete()
    {
        BettingRound round = new BettingRound(2);

        round.setRoundComplete(true);

        assertTrue(round.isRoundComplete());
    }

    @Test
    public void testReset()
    {
        BettingRound round = new BettingRound(2);

        round.setCurrentBet(20);
        round.playerActed();
        round.setRoundComplete(true);

        round.reset();

        assertEquals(0, round.getCurrentBet());
        assertEquals(0, round.getPlayersActed());
        assertFalse(round.isRoundComplete());
    }

    @Test
    public void testInvalidMinimumRaise()
    {
        try
        {
            new BettingRound(0);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // Expected
        }
    }

    @Test
    public void testNegativeCurrentBet()
    {
        BettingRound round = new BettingRound(2);

        try
        {
            round.setCurrentBet(-1);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(0, round.getCurrentBet());
        }
    }

    @Test
    public void testInvalidRaise()
    {
        BettingRound round = new BettingRound(5);

        round.setCurrentBet(10);

        try
        {
            round.recordRaise(12);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(10, round.getCurrentBet());
        }
    }
}