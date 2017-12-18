/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");
    private static final Instant d4 = Instant.parse("2016-02-17T13:00:00Z");
    private static final Instant d5 = Instant.parse("2016-02-17T14:00:00Z");
    private static final Instant d6 = Instant.parse("2016-02-17T15:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "boy! that expectations though", d3);
    private static final Tweet tweet4 = new Tweet(4, "bbitdiddle", "hope i am not forgetting anything", d1);
    private static final Tweet tweet5 = new Tweet(5, "ALYSSA", "look at bbitdiddle sway, a definite treat to watch", d4);
    private static final Tweet tweet6 = new Tweet(6, "bbitdiddle", "didn't @alyssa take @ALYSSA on a trip", d5);
    private static final Tweet tweet7 = new Tweet(7, "alyssa", "came after @ long pause", d5);
    private static final Tweet tweet8 = new Tweet(8, "bbitdiddle", "@curious_player-dead's name is good", d5);
    private static final Tweet tweet9 = new Tweet(9, "ALYSSA", "i am tired #@bbitdiddle!", d6);
    private static final Tweet tweet10 = new Tweet(10, "bbitdiddle", "hopef@lly, i come out fine, @alyssa@eah, @@finally", d6);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /**
     * 1. empty username
     * 2. lower and uppercase username
     * 3. empty list of tweets
     * 4. lower and uppercase of username in tweets
     */
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
        
        // Custom tests
        List<Tweet> writtenBy1 = Filter.writtenBy(Arrays.asList(tweet3, tweet4, tweet5, 
                tweet6, tweet7, tweet8, tweet9, tweet10), "");
        List<Tweet> resultList1 = Collections.emptyList();
        assertEquals("empty username", resultList1, writtenBy1);
        
        List<Tweet> writtenBy2 = Filter.writtenBy(Arrays.asList(tweet3, tweet4, tweet5, 
                tweet6, tweet7, tweet8, tweet9, tweet10), "alyssa");
        List<Tweet> resultList2 = Arrays.asList(tweet3, tweet5, tweet7, tweet9);
        assertEquals("lower case username", resultList2, writtenBy2);
        
        List<Tweet> writtenBy3 = Filter.writtenBy(Arrays.asList(tweet3, tweet4, tweet5, 
                tweet6, tweet7, tweet8, tweet9, tweet10), "ALYSSA");
        List<Tweet> resultList3 = Arrays.asList(tweet3, tweet5, tweet7, tweet9);
        assertEquals("lower case username", resultList3, writtenBy3);
        
        List<Tweet> writtenBy4 = Filter.writtenBy(Collections.emptyList(), "alyssa");
        List<Tweet> resultList4 = Collections.emptyList();
        assertEquals("empty tweet list", resultList4, writtenBy4);
    }
    
    /**
     * 1. single timestamp in timespan
     * 2. no tweets in timestamp
     * 3. boundary
     */
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
        
        // Custom tests
        List<Tweet> inTimespan1 = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3, tweet4), new Timespan(d1, d1));
        List<Tweet> resultList1 = Arrays.asList(tweet1, tweet4);
        assertEquals("single timestamp", resultList1, inTimespan1);
        
        List<Tweet> inTimespan2 = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5), 
                new Timespan(d5, d6));
        List<Tweet> resultList2 = Collections.emptyList();
        assertEquals("single timestamp", resultList2, inTimespan2);
    }
    
    /**
     * 1. no word in word list
     * 2. no matching word in tweets
     * 3. multiple tweets same matching word
     * 4. lowercase, uppercase of words in tweets
     * 5. same words - lowercase, uppercase in word list
     * 6. word containing special character in tweet
     */
    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
        
        List<Tweet> containing1 = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5, 
                tweet6, tweet7, tweet8, tweet9, tweet10), Collections.emptyList());
        List<Tweet> resultList1 = Collections.emptyList();
        assertEquals("empty word list", resultList1, containing1);
        
        List<Tweet> containing2 = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5, 
                tweet6, tweet7, tweet8, tweet9, tweet10), Arrays.asList("boy", "hopef@lly"));
        List<Tweet> resultList2 = Collections.emptyList();
        assertEquals("no matching word, special character", resultList2, containing2);
        
        List<Tweet> containing3 = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5, 
                tweet6, tweet7, tweet8, tweet9, tweet10), Arrays.asList("@alyssa"));
        List<Tweet> resultList3 = Arrays.asList(tweet6);
        assertEquals("lowercase, uppercase of words in tweets", resultList3, containing3);
        
        List<Tweet> containing4 = Filter.containing(Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5, 
                tweet6, tweet7, tweet8, tweet9, tweet10), Arrays.asList("RIVEST"));
        List<Tweet> resultList4 = Arrays.asList(tweet1, tweet2);
        assertEquals("multiple tweets same word, uppercase in word list", resultList4, containing4);
    }

    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
