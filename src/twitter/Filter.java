/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Filter consists of methods that filter a list of tweets for those matching a
 * condition.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Filter {

    /**
     * Find tweets written by a particular user.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param username
     *            Twitter username, required to be a valid Twitter username as
     *            defined by Tweet.getAuthor()'s spec.
     * @return all and only the tweets in the list whose author is username,
     *         in the same order as in the input list.
     */
    public static List<Tweet> writtenBy(List<Tweet> tweets, String username) {
        List<Tweet> resultList = new ArrayList<>();
        
        for (Tweet tweet : tweets)
        {
            if (tweet.getAuthor().equalsIgnoreCase(username))
            {
                resultList.add(tweet);
            }
        }
        
        return resultList;
    }

    /**
     * Find tweets that were sent during a particular timespan.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param timespan
     *            timespan
     * @return all and only the tweets in the list that were sent during the timespan,
     *         in the same order as in the input list.
     */
    public static List<Tweet> inTimespan(List<Tweet> tweets, Timespan timespan) {
        List<Tweet> resultList = new ArrayList<Tweet>();
        
        for (Tweet tweet : tweets)
        {
            if (tweet.getTimestamp().compareTo(timespan.getStart()) >= 0 
                    && tweet.getTimestamp().compareTo(timespan.getEnd()) <= 0)
            {
                resultList.add(tweet);
            }
        }
        
        return resultList;
    }
    
    private static boolean containing(String text, Set<String> lookupSet)
    {
        String[] words = text.split("\\s+");
        
        for (String word : words)
        {
            if (lookupSet.contains(word))
            {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Find tweets that contain certain words.
     * 
     * @param tweets
     *            a list of tweets with distinct ids, not modified by this method.
     * @param words
     *            a list of words to search for in the tweets. 
     *            A word is a nonempty sequence of nonspace characters.
     * @return all and only the tweets in the list such that the tweet text (when 
     *         represented as a sequence of nonempty words bounded by space characters 
     *         and the ends of the string) includes *at least one* of the words 
     *         found in the words list. Word comparison is not case-sensitive,
     *         so "Obama" is the same as "obama".  The returned tweets are in the
     *         same order as in the input list.
     */
    public static List<Tweet> containing(List<Tweet> tweets, List<String> words) {
        Set<String> lookupSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        lookupSet.addAll(words);
        List<Tweet> resultList = new ArrayList<>();
        
        for (Tweet tweet : tweets)
        {
            if (containing(tweet.getText(), lookupSet))
            {
                resultList.add(tweet);
            }
        }
        
        return resultList;
    }

}
