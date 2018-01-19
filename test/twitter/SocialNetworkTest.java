/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "user1", "My inner self says, @user1 you are ready.", d1);
    private static final Tweet tweet2 = new Tweet(2, "user2", "When sleeping I was in doubt, @user2 have you completed the job.", d1);
    private static final Tweet tweet3 = new Tweet(3, "UseR3", "Now how do I explain the situation, @USER4?", d1);
    private static final Tweet tweet4 = new Tweet(4, "uSEr4", "Boy! Aren't you worked up, @user3?", d1);
    private static final Tweet tweet5 = new Tweet(5, "USER5", "Can you pass the message to @UseR3, @uSEr6?", d1);
    private static final Tweet tweet6 = new Tweet(6, "user6", "Hi, @user3. Please don't get worked up. It is all good fun, you see", d1);
    private static final Tweet tweet7 = new Tweet(7, "user6", "I can't find @user7. Can anyone tell me his location? @user4?", d1);
    
    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    /**
     * 1. empty list of tweets
     * 2. no dependancy
     * 3. mentions himself
     * 4. circular dependancy
     * 5. non-circular dependancy
     * 6. mentions users by various cases 
     * 7. case change in tweet metadata
     * 8. multiple tweets with same user
     */
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
        
        // Custom tests
        Map<String, Set<String>> followsGraph1 = SocialNetwork.guessFollowsGraph(
                Arrays.asList(tweet1, tweet2));
        Map<String, Set<String>> resultGraph1 = Collections.emptyMap();
        assertEquals("no dependancies, mentions himself", SocialNetwork.reduceMap(resultGraph1), 
                SocialNetwork.reduceMap(followsGraph1));
        
        Map<String, Set<String>> followsGraph2 = SocialNetwork.guessFollowsGraph(
                Arrays.asList(tweet3, tweet4, tweet5, tweet6));
        Map<String, Set<String>> resultGraph2 = new HashMap<>();
        resultGraph2.put("user3", new HashSet<>(Arrays.asList("user4")));
        resultGraph2.put("user4", new HashSet<>(Arrays.asList("user3")));
        resultGraph2.put("user5", new HashSet<>(Arrays.asList("user3", "user6")));
        resultGraph2.put("user6", new HashSet<>(Arrays.asList("user3")));
        assertEquals("circular dependancy", SocialNetwork.reduceMap(resultGraph2), 
                SocialNetwork.reduceMap(followsGraph2));
        
        Map<String, Set<String>> followsGraph3 = SocialNetwork.guessFollowsGraph(
                Arrays.asList(tweet3, tweet5, tweet6));
        Map<String, Set<String>> resultGraph3 = new HashMap<>();
        resultGraph3.put("user3", new HashSet<>(Arrays.asList("user4")));
        resultGraph3.put("user5", new HashSet<>(Arrays.asList("user3", "user6")));
        resultGraph3.put("user6", new HashSet<>(Arrays.asList("user3")));
        assertEquals("no cirular dependancies", SocialNetwork.reduceMap(resultGraph3), 
                SocialNetwork.reduceMap(followsGraph3));
        
        Map<String, Set<String>> followsGraph4 = SocialNetwork.guessFollowsGraph(
                Arrays.asList(tweet6, tweet7));
        Map<String, Set<String>> resultGraph4 = new HashMap<>();
        resultGraph4.put("user6", new HashSet<>(Arrays.asList("user3", "user4", "user7")));
        assertEquals("circular dependancy", SocialNetwork.reduceMap(resultGraph4), 
                SocialNetwork.reduceMap(followsGraph4));
    }
    
    /**
     * 1. Empty graph
     * 2. Graph containing just empty sets
     * 3. Same number of followers
     * 4. Distinct number of followers
     */
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
        
        // Custom tests
        Map<String, Set<String>> followsGraph1 = new HashMap<>();
        followsGraph1.put("user1", Collections.emptySet());
        List<String> influencers1 = SocialNetwork.influencers(followsGraph1);
        assertTrue("just empty sets", SocialNetwork.isSorted(influencers1, 
                SocialNetwork.invertGraph(followsGraph1)) && influencers1.size() == 0);
        
        Map<String, Set<String>> followsGraph2 = new HashMap<>();
        followsGraph2.put("user1", new HashSet<String>(Arrays.asList("user2", "user3")));
        followsGraph2.put("user2", new HashSet<String>(Arrays.asList("user3", "user1")));
        followsGraph2.put("user3", new HashSet<String>(Arrays.asList("user1", "user2")));
        List<String> influencers2 = SocialNetwork.influencers(followsGraph2);
        assertTrue("just empty sets", SocialNetwork.isSorted(influencers2, 
                SocialNetwork.invertGraph(followsGraph2)) && influencers2.size() == 3);
        
        Map<String, Set<String>> followsGraph3 = new HashMap<>();
        followsGraph3.put("user1", new HashSet<String>(Arrays.asList("user2", "user3", "user4")));
        followsGraph3.put("user2", new HashSet<String>(Arrays.asList("user3", "user4")));
        followsGraph3.put("user3", new HashSet<String>(Arrays.asList("user4")));
        List<String> influencers3 = SocialNetwork.influencers(followsGraph3);
        assertTrue("just empty sets", SocialNetwork.isSorted(influencers3, 
                SocialNetwork.invertGraph(followsGraph3)) && influencers3.size() == 3);
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}
