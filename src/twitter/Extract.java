/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        if (tweets.size() == 0)
        {
            throw new IllegalArgumentException("Atleast one tweet must exist");
        }
        
        Iterator<Tweet> iter = tweets.iterator();
        Instant start = iter.next().getTimestamp();
        Instant end = start;
        
        while (iter.hasNext())
        {
            Instant local = iter.next().getTimestamp();
            
            if (local.compareTo(start) < 0)
            {
                start = local;
            }
            
            if (local.compareTo(end) > 0)
            {
                end = local;
            }
        }
        
        return new Timespan(start, end);
    }
    
    /**
     * 
     * @param tweet single tweet, not modified by this method
     * @return set of usernames present in the tweet
     */
    private static Set<String> getMentionedUsers(Tweet tweet)
    {
        Set<String> resultSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        String[] words = tweet.getText().toLowerCase().split("[^\\w@-]+");
        
        for (String word : words)
        {
            if (word.startsWith("@"))
            {
                String uname;
                int length = word.length();
                int startIndex;
                
                for (startIndex = 0; startIndex < length && word.charAt(startIndex) == '@'; startIndex++);
                int endIndex = word.indexOf('@', startIndex);
                
                if (endIndex == -1)
                {
                    uname = word.substring(startIndex);
                }
                else
                {
                    uname = word.substring(startIndex, endIndex);
                }
                
                if (!uname.isEmpty())
                {
                    resultSet.add(uname);
                }
            }
        }
        
        return resultSet;
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> resultSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        
        for (Tweet tweet : tweets)
        {
            resultSet.addAll(getMentionedUsers(tweet));
        }
        
        return resultSet;
    }

}
