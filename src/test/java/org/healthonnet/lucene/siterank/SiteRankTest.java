package org.healthonnet.lucene.siterank;

import org.healthonnet.lucene.siterank.source.AlexaSiteRankSource;
import org.healthonnet.lucene.siterank.source.SiteRankSource;
import org.junit.Assert;
import org.junit.Test;

public class SiteRankTest {

    @Test
    public void testSiteRank1() {
        Assert.assertEquals(1, new MockSiteRankSource().getRankInfo("google.com").getRank());
        Assert.assertEquals(430, new MockSiteRankSource().getRankInfo("webmd.com").getRank());
        Assert.assertEquals(4706956, new MockSiteRankSource().getRankInfo("thebuttercompartment.com").getRank());
        
    }
    
    @Test
    public void testSiteRankWithHostParsing() {
        SiteRankSource siteRankSource = new MockSiteRankSource();
        siteRankSource.setExtractDomainFromUrl(true);
        
        Assert.assertEquals(1, siteRankSource.getRankInfo("http://google.com/mail").getRank());
        Assert.assertEquals(430, siteRankSource.getRankInfo("http://webmd.com").getRank());
        Assert.assertEquals(4706956, siteRankSource.getRankInfo("http://thebuttercompartment.com/some/page").getRank());
    }
    
    @Test
    public void testAlexaUrls() {
        Assert.assertEquals("http://data.alexa.com/data?cli=10&url=google.com", 
                new AlexaSiteRankSource().buildURI("google.com").toString());
        Assert.assertEquals("http://data.alexa.com/data?cli=10&url=thebuttercompartment.com", 
                new AlexaSiteRankSource().buildURI("thebuttercompartment.com").toString());
    }
}
