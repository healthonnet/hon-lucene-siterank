package org.healthonnet.lucene.siterank;

import java.io.File;
import java.net.URI;

import org.healthonnet.lucene.siterank.source.AlexaSiteRankSource;

public class MockSiteRankSource extends AlexaSiteRankSource {

    private static enum KnownUrl {
        buttercompartment("thebuttercompartment.com"), 
        google ("google.com"), 
        webmd ("webmd.com");
        private String urlString;
        private KnownUrl(String urlString) {
            this.urlString = urlString;
        }
        
        public static KnownUrl findByUrl(String urlString) {
            for (KnownUrl knownUrl : values()) {
                if (knownUrl.urlString.equals(urlString)) {
                    return knownUrl;
                }
            }
            return null;
        }
        
    }
    
    public URI buildURI(String urlString) {
        KnownUrl knownUrl = KnownUrl.findByUrl(urlString);
        File file = new File(getClass().getClassLoader().getResource(knownUrl.name() + ".xml").getFile());
        return file.toURI();
    }
}
