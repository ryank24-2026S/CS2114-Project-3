package poker;
import org.junit.Test;
import static org.junit.Assert.*;
public class TurnQueueTest
{
    @Test
    public void testEnqueueAndDequeue()
    {
        TurnQueue queue = new TurnQueue();

        queue.enqueue("Alice");
        queue.enqueue("Bob");

        assertEquals("Alice", queue.dequeue());
        assertEquals("Bob", queue.dequeue());
        assertTrue(queue.isEmpty());
    }

    @Test
    public void testPeek()
    {
        TurnQueue queue = new TurnQueue();

        queue.enqueue("Alice");
        queue.enqueue("Bob");

        assertEquals("Alice", queue.peek());
        assertEquals(2, queue.size());
    }

    @Test
    public void testQueueSize()
    {
        TurnQueue queue = new TurnQueue();

        queue.enqueue("Alice");
        queue.enqueue("Bob");
        queue.enqueue("Charlie");

        assertEquals(3, queue.size());
    }

    @Test
    public void testClear()
    {
        TurnQueue queue = new TurnQueue();

        queue.enqueue("Alice");
        queue.enqueue("Bob");

        queue.clear();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    public void testEmptyPeek()
    {
        TurnQueue queue = new TurnQueue();

        try
        {
            queue.peek();
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e)
        {
            assertNotNull(e);
        }
    }

    @Test
    public void testEmptyDequeue()
    {
        TurnQueue queue = new TurnQueue();

        try
        {
            queue.dequeue();
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e)
        {
            assertNotNull(e);
        }
    }

    @Test
    public void testNullName()
    {
        TurnQueue queue = new TurnQueue();

        try
        {
            queue.enqueue(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(queue.isEmpty());
        }
    }

    @Test
    public void testBlankName()
    {
        TurnQueue queue = new TurnQueue();

        try
        {
            queue.enqueue(" ");
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(queue.isEmpty());
        }
    }

    @Test
    public void testDuplicatePlayer()
    {
        TurnQueue queue = new TurnQueue();

        queue.enqueue("Alice");

        try
        {
            queue.enqueue("Alice");
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals(1, queue.size());
        }
    }
}