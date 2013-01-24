package org.healthonnet.lucene.siterank;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.ValueSourceParser;
import org.apache.solr.search.function.DocValues;
import org.apache.solr.search.function.ValueSource;
import org.healthonnet.lucene.siterank.source.AlexaSiteRankSource;
import org.healthonnet.lucene.siterank.source.CachingSiteRankSource;
import org.healthonnet.lucene.siterank.source.SiteRankSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ValueSourceParser that uses the site rank of a given URL to produce a double between 0.0 and 1.0.  Probably
 * you will want to wrap the output of this function in an exp() function or something, 
 * in order to smooth the distribution.
 * 
 * <p/>So the recommended usage is e.g. <code>bf=exp(siterank(myDomainField))</code>
 *
 * <p/>The output score is a combination of the rank and the total number of sites, equaling
 * <code>(totalNum - (rank -1)) / totalNum</code>
 * 
 * <p/>For instance, the 1st ranked site out of 1000 would give 1.0, and the last-ranked would give (1/1000) = 0.0001.
 * Unranked sites return 0.0.
 * 
 * @author nolan
 *
 */
public class SiteRankSourceParser extends ValueSourceParser {

    private static final Logger LOG = LoggerFactory.getLogger(SiteRankSourceParser.class);
    
    private static enum Params {
        doCache, cacheSpec, extractDomainFromUrl;
    }
    
    /**
     * @see http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/cache/CacheBuilderSpec.html
     * for how to build this spec.
     * 
     * These are supposed to be sensible defaults for a no-config SiteRankSourceParser.
     */
    private static final String DEFAULT_CACHE_SPEC = "concurrencyLevel=16,maximumSize=8192,softValues";
    
    private SiteRankSource siteRankSource;
    
    @SuppressWarnings("rawtypes")
    @Override
    public void init(NamedList args) {
        super.init(args);
        
        Object doCacheObject = args.get(Params.doCache.name());
        
        // doCache is true by default
        boolean doCache = doCacheObject == null 
                || !(doCacheObject instanceof Boolean) 
                || ((Boolean)doCacheObject);
        
        Object cacheSpecObject = args.get(Params.cacheSpec.name());
        
        String cacheSpec = cacheSpecObject != null && cacheSpecObject instanceof String 
                ? (String) cacheSpecObject 
                : DEFAULT_CACHE_SPEC;
                
        Object extractDomainObject = args.get(Params.extractDomainFromUrl.name());
        
        // extract domain from url is true by default
        boolean extractDomainFromUrl = extractDomainObject == null
                || !(extractDomainObject instanceof Boolean)
                || ((Boolean)extractDomainObject);
        
        // TODO: make Alexa vs. some other source configurable
        siteRankSource = new AlexaSiteRankSource();
        
        if (doCache) {
            siteRankSource = new CachingSiteRankSource(siteRankSource, cacheSpec);
        }
        
        siteRankSource.setExtractDomainFromUrl(extractDomainFromUrl);
        
        LOG.info("Initializing SiteRankSourceParser with " + siteRankSource);
    }
    
    @Override
    public ValueSource parse(FunctionQParser functionQParser) throws ParseException {
        ValueSource source = functionQParser.parseValueSource();
        String url = functionQParser.parseArg();
        return new SiteRankFunction(source, url);
    }

    private class SiteRankFunction extends ValueSource {

        private static final long serialVersionUID = -1816500469014846528L;

        ValueSource valueSource;
        
        public SiteRankFunction(ValueSource valueSource, String url) {
            this.valueSource = valueSource;
        }
        
        @SuppressWarnings("rawtypes")
        @Override
        public DocValues getValues(Map context, IndexReader reader) throws IOException {
            final DocValues docValues = valueSource.getValues(context, reader);
            return new DocValues() {
                
                @Override
                public String toString(int doc) {
                    return "siterank(" + docValues.toString(doc) + ")";
                }

                @Override
                public short shortVal(int doc) {
                    return (short)doubleVal(doc);
                }

                @Override
                public float floatVal(int doc) {
                    return (float)doubleVal(doc);
                }

                @Override
                public int intVal(int doc) {
                    return (int)doubleVal(doc);
                }

                @Override
                public long longVal(int doc) {
                    return (long)doubleVal(doc);
                }

                @Override
                public double doubleVal(int doc) {
                    SiteRankInfo siteRankInfo = siteRankSource.getRankInfo(docValues.strVal(doc));
                    if (siteRankInfo.isSiteFound()) {
                        return 1.0 / siteRankInfo.getRank(); //reciprocal rank
                    }
                    return 0.0; // not found
                }

                @Override
                public String strVal(int doc) {
                    return String.valueOf(doubleVal(doc));
                }
            };
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            return true;
        }

        @Override
        public String description() {
            return "siterank function";
        }
    }
}
