package org.healthonnet.lucene.siterank.source;

import org.healthonnet.lucene.siterank.SiteRankInfo;


/**
 * Abstract interface for a source of site rank information.
 * A SiteRankSource is an object that tells us where to get information about the site rank of a given URL.
 * @author nolan
 *
 */
public interface SiteRankSource {
    
    /**
     * Get the rank of a given URL as a SiteRankInfo object.  For instance, "google.com" on Alexa would return 1.
     * The SiteRankInfo object also contains information about how many URLs are in this source, total, and whether
     * the url was even found.
     * 
     * @param url
     * @return the rank info
     */
    public SiteRankInfo getRankInfo(String inputUrl);
    
    /**
     * Tell the SiteRankSource whether it should extract the domain from the url or not.
     * 
     * @param extractDomainFromUrl
     */
    public void setExtractDomainFromUrl(boolean extractDomainFromUrl);
    
}
