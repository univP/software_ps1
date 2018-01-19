/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> resultGraph = new HashMap<>();
        
        for (Tweet tweet : tweets)
        {
            String author = tweet.getAuthor().toLowerCase();
            Set<String> value = reduceSet(Extract.getMentionedUsers(Arrays.asList(tweet)));
            
            if (value.contains(author))
            {
                value.remove(author);
            }
            
            if (resultGraph.containsKey(author))
            {
                resultGraph.get(author).addAll(value);
            }
            else
            {
                resultGraph.put(author, value);
            }
        }
        
        return resultGraph;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Set<String>> followedByGraph = invertGraph(followsGraph);
        List<String> resultList = new ArrayList<>();
        
        for (Map.Entry<String, Set<String>> vertex : followedByGraph.entrySet())
        {
            if (!vertex.getValue().isEmpty())
            {
                resultList.add(vertex.getKey());
            }
        }
        
        Collections.sort(resultList, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                return followedByGraph.get(o2).size() - followedByGraph.get(o1).size();
            }
            
        });
        
        return resultList;
    }
    
    /**
     * 
     * @param neighbours set from which we extract the usernames, may not be modified
     * @return the same set in lowercase
     * @throws IllegalArgumentException when duplicate usernames present, username is case insensitive
     */
    public static Set<String> reduceSet(Set<String> neighbours)
    {
        Set<String> resultSet = new HashSet<String>();
        
        for (String neighbour : neighbours)
        {
            String value = neighbour.toLowerCase();
            
            if (resultSet.contains(value))
            {
                throw new IllegalArgumentException("Duplicate username in set");
            }
            
            resultSet.add(value);
        }
        
        return resultSet;
    }
    
    /**
     * 
     * @param graph from which we extract the usernames and whom they follow, may not be modified
     * @return all usernames in lowercase and remove empty sets
     * @throws IllegalArgumentException when duplicate usernames are found, username is case insensitive
     */
    public static Map<String, Set<String>> reduceMap(Map<String, Set<String>> graph)
    {
        Map<String, Set<String>> resultGraph = new HashMap<String, Set<String>>();
        
        for (Map.Entry<String, Set<String>> vertex : graph.entrySet())
        {
            String key = vertex.getKey().toLowerCase();
            Set<String> value = reduceSet(vertex.getValue());
            
            if (resultGraph.containsKey(key))
            {
                throw new IllegalArgumentException("Duplicate username in graph");
            }
            
            if (!value.isEmpty())
            {
                resultGraph.put(key, value);
            }
        }
        
        return resultGraph;
    }
    
    /**
     * 
     * @param followsGraph graph representing all the followers of a user
     * @return the inverted graph in reduced form (all lowercase and empty users removed)
     *          i.e. a vertex is present for a user iff he has atleast one follower
     */
    public static Map<String, Set<String>> invertGraph(Map<String, Set<String>> followsGraph)
    {
        Map<String, Set<String>> reducedGraph = reduceMap(followsGraph);
        Map<String, Set<String>> followedByGraph = new HashMap<>();
        
        for (Map.Entry<String, Set<String>> vertex : reducedGraph.entrySet())
        {
            String follower = vertex.getKey();
            Set<String> reducedSet = reduceSet(vertex.getValue());
            
            for (String user : reducedSet)
            {
                if (followedByGraph.containsKey(user))
                {
                    followedByGraph.get(user).add(follower);
                }
                else
                {
                    followedByGraph.put(user, new HashSet<String>(Arrays.asList(follower)));
                }
            }
        }
        
        return followedByGraph;
    }
    
    /**
     * 
     * @param influencers check whether this list is sorted in descending order, may not be modified
     * @param followedByGraph the inverted graph representing the followers of a person, 
     *         need to be in reduced form, may not be modified
     * @return whether the given list is in fact sorted or not (does not need to be exhaustive)
     */
    public static boolean isSorted(List<String> influencers, Map<String, Set<String>> followedByGraph)
    {        
        if (!influencers.isEmpty())
        {
            Iterator<String> iter = influencers.iterator();
            String author = iter.next().toLowerCase();
            
            if (!followedByGraph.containsKey(author))
            {
                throw new IllegalArgumentException("Author not present in the graph");
            }
            
            int maxSize = followedByGraph.get(author).size();
            
            while (iter.hasNext())
            {
                author = iter.next().toLowerCase();
                
                if (!followedByGraph.containsKey(author))
                {
                    return false;
                }
                
                int nextSize = followedByGraph.get(author).size();
                
                if (nextSize > maxSize)
                {
                    return false;
                }
                
                maxSize = nextSize;
            }
        }
        
        return true;
    }
}
