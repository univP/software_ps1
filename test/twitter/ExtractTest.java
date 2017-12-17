/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class ExtractTest {

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
    private static final Tweet tweet5 = new Tweet(5, "alyssa", "look at bbitdiddle sway, a definite treat to watch", d4);
    private static final Tweet tweet6 = new Tweet(6, "bbitdiddle", "didn't @alyssa take @ALYSSA on a trip", d5);
    private static final Tweet tweet7 = new Tweet(7, "alyssa", "came after @ long pause", d5);
    private static final Tweet tweet8 = new Tweet(8, "bbitdiddle", "@curious_player-dead's name is good", d5);
    private static final Tweet tweet9 = new Tweet(9, "alyssa", "i am tired #@bbitdiddle!", d6);
    private static final Tweet tweet10 = new Tweet(10, "bbitdiddle", "hopef@lly, i come out fine, @alyssa@eah, @@finally", d6);
    
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /**
     * 1. No tweets - not allowed
     * 2. Only single tweet
     * 3. Several tweets with same value
     * 4. Ascending order of tweets
     * 5. Descending order of tweets
     */
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
        
        // Custom tests
        Timespan timespan2 = Extract.getTimespan(Arrays.asList(tweet1));
        assertEquals("expected start", d1, timespan2.getStart());
        assertEquals("expected end", d1, timespan2.getEnd());
        
        Timespan timespan3 = Extract.getTimespan(Arrays.asList(tweet1, tweet4));
        assertEquals("expected start", d1, timespan3.getStart());
        assertEquals("expected end", d1, timespan3.getEnd());
        
        Timespan timespan4 = Extract.getTimespan(Arrays.asList(tweet1, tweet2, tweet3));
        assertEquals("expected start", d1, timespan4.getStart());
        assertEquals("expected end", d3, timespan4.getEnd());
        
        Timespan timespan5 = Extract.getTimespan(Arrays.asList(tweet3, tweet2, tweet1));
        assertEquals("expected start", d1, timespan5.getStart());
        assertEquals("expected end", d3, timespan5.getEnd());
    }
    
    /**
     * 1. no username in text
     * 2. multiple instances of same username - case change
     * 3. usernames without @
     * 4. empty username
     * 5. username containing special symbols like #, !
     * 6. usernames containing _, -
     * 7. usernames containing @
     * 
     * grouping - (1, 3), (2, 4, 6), (5, 7)
     */
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
        
        // Custom tests
        Set<String> mentionedUsers1 = Extract.getMentionedUsers(Arrays.asList(tweet5));
        assertTrue("expected empty set", mentionedUsers1.isEmpty());
        
        Set<String> mentionedUsers2 = Extract.getMentionedUsers(Arrays.asList(tweet6, tweet7, tweet8));
        Set<String> resultUsers2 = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        resultUsers2.addAll(Arrays.asList("alyssa", "curious_player-dead"));
        assertEquals("expected users in code", resultUsers2, mentionedUsers2);
        
        Set<String> mentionedUsers3 = Extract.getMentionedUsers(Arrays.asList(tweet9, tweet10));
        Set<String> resultUsers3 = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        resultUsers3.addAll(Arrays.asList("alyssa", "bbitdiddle", "finally"));
        assertEquals("expected users in code", resultUsers3, mentionedUsers3);
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}
