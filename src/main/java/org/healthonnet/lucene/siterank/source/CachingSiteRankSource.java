package org.healthonnet.lucene.siterank.source;

import java.util.concurrent.ExecutionException;

import org.healthonnet.lucene.siterank.SiteRankInfo;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Wrapper around a SiteRankSource that does some smart caching using Google Guava Caches.
 * @author nolan
 *
 */
public class CachingSiteRankSource implements SiteRankSource {

    private SiteRankSource innerSiteRankSource;
    private String cacheSpec;
    
    private LoadingCache<String, SiteRankInfo> cache;
    
    /**
     * Create a new CachingSiteRankSource, given an inner source and a Guava-style Cache spec
     * @param innerSiteRankSource
     * @param cacheSpec
     */
    public CachingSiteRankSource(SiteRankSource innerSiteRankSource, String cacheSpec) {
        Preconditions.checkArgument(!(innerSiteRankSource instanceof CachingSiteRankSource), 
                "innerSiteRankSource must not itself be a CachingSiteRankSource");
        this.innerSiteRankSource = innerSiteRankSource;
        this.cacheSpec = cacheSpec;
        
        initCache();
    }

    private void initCache() {
        cache = CacheBuilder.from(cacheSpec)
                .build(new CacheLoader<String, SiteRankInfo>() {

                    @Override
                    public SiteRankInfo load(String input) throws Exception {
                        return Preconditions.checkNotNull(innerSiteRankSource.getRankInfo(input));
                    }
                });
    }

    public SiteRankInfo getRankInfo(String inputUrl) {
        try {
            return cache.get(inputUrl);
        } catch (ExecutionException e) {
            throw Throwables.propagate(e);
        }
    }

    public void setExtractDomainFromUrl(boolean extractDomainFromUrl) {
        innerSiteRankSource.setExtractDomainFromUrl(extractDomainFromUrl);
    }

    @Override
    public String toString() {
        return "CachingSiteRankSource [innerSiteRankSource=" + innerSiteRankSource 
                + ", cacheSpec=" + cacheSpec + "]";
    }
}
