package org.healthonnet.lucene.siterank.source;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.utils.URIBuilder;

import com.google.common.base.Throwables;

public class AlexaSiteRankSource extends AbstractSiteRankSource {
    
    // undocumented Alexa site rank api
    private static final String BASE_URL = "http://data.alexa.com/data";
    private static final Pattern SITE_RANK_PATTERN = Pattern.compile("RANK=\"(\\d+)\"");
    
    // TODO: actually determine how many sites are in Alexa... for now, we just guess, and update our assumption
    // in case we see a site that's even lower
    private static final int INITIAL_TOTAL_NUM_SITES = 50000000;
    
    private int totalNumSites = INITIAL_TOTAL_NUM_SITES;
    
    public URI buildURI(String url) {
        try {
            return new URIBuilder(BASE_URL)
                .addParameter("cli", "10")
                .addParameter("url", url)
                .build();
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }
    }

    public int parseRank(String result, int defaultValue) {
        Matcher matcher = SITE_RANK_PATTERN.matcher(result);
        if (matcher.find()) {
            int rank = Integer.valueOf(matcher.group(1));
            if (rank > totalNumSites) {
                totalNumSites = rank; // update our guess
            }
            return rank;
        }
        return defaultValue;
    }

    @Override
    protected int getTotal() {
        return totalNumSites;
    }
}
