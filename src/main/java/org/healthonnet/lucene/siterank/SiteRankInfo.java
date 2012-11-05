package org.healthonnet.lucene.siterank;

/**
 * POJO representing some rank info about a site.
 * @author nolan
 *
 */
public class SiteRankInfo {

    private int rank;
    private int totalNumSites;
    private boolean siteFound;
    
    
    public SiteRankInfo(int rank, int totalNumSites, boolean siteFound) {
        this.rank = rank;
        this.totalNumSites = totalNumSites;
        this.siteFound = siteFound;
    }
    
    public int getRank() {
        return rank;
    }
    public int getTotalNumSites() {
        return totalNumSites;
    }
    
    public boolean isSiteFound() {
        return siteFound;
    }

    @Override
    public String toString() {
        return "SiteRankInfo [rank=" + rank + ", totalNumSites=" + totalNumSites + ", siteFound=" + siteFound + "]";
    }
}
