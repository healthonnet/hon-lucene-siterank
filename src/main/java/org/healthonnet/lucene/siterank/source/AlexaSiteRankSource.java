package org.healthonnet.lucene.siterank.source;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

import com.google.common.base.Throwables;

public class AlexaSiteRankSource extends AbstractSiteRankSource {
    
    // undocumented Alexa site rank api
    private static final String BASE_URL = "http://data.alexa.com/data";
    private static final Pattern SITE_RANK_PATTERN = Pattern.compile("RANK=\"(\\d+)\"");
    
    public URI buildURI(String url) {
        try {
            StringBuilder stringBuilder = new StringBuilder(BASE_URL)
                .append("?cli=")
                .append(URIUtil.encodeQuery("10"))
                .append("&url=")
                .append(URIUtil.encodeQuery(url));
            
            return new URI(stringBuilder.toString());
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        } catch (URIException e) {
            throw Throwables.propagate(e);
        }
    }

    public int parseRank(String result, int defaultValue) {
        Matcher matcher = SITE_RANK_PATTERN.matcher(result);
        if (matcher.find()) {
            int rank = Integer.valueOf(matcher.group(1));
            return rank;
        }
        return defaultValue;
    }

}
