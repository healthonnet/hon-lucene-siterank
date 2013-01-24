package org.healthonnet.lucene.siterank;

/**
 * POJO representing some rank info about a site.
 * @author nolan
 *
 */
public class SiteRankInfo {

    private int rank;
    private boolean siteFound;
    
    
    public SiteRankInfo(int rank, boolean siteFound) {
        this.rank = rank;
        this.siteFound = siteFound;
    }
    
    public int getRank() {
        return rank;
    }
    
    public boolean isSiteFound() {
        return siteFound;
    }

    @Override
    public String toString() {
        return "SiteRankInfo [rank=" + rank + ", totalNumSites=" + siteFound + "]";
    }
}
