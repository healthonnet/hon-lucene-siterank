Lucene/Solr Site Rank Function
=========================

Developer
-----------

[Nolan Lawson][7]

[Health On the Net Foundation][6]

License
-----------

[Apache 2.0][1]

Summary
-----------

Custom Solr [Function Query][3] that returns the [Alexa][4] site rank of an input URL, host, or domain. This can be useful for boosting web documents based on their Alexa rank.

In other words, it's a poor man's [PageRank][5].

This module also performs some light caching, in order to show good etiquette to Alexa, who probably don't want folks hammering their servers.

Setup
------------

First off, put the following JARs into your Solr's lib/ directory:

* [hon-lucene-siterank-1.1.jar][8]
* [guava-13.0.1.jar][9]

(Yes, I'm requiring Google Guava for this.  It helps protect my sanity when I code in Java these days.)

Next, add the following definition to your <code>solrconfig.xml</code>:

```xml
<valueSourceParser name="siterank" class="org.healthonnet.lucene.siterank.SiteRankSourceParser">
    <bool name="doCache">true</bool>
    <str name="cacheSpec">concurrencyLevel=16,maximumSize=8192,softValues</str>
    <bool name="extractDomainFromUrl">true</bool>
</valueSourceParser>
```

Shown above are all the configuration parameters with their default values. You can leave them out if you're okay with the defaults.

Parameters
----------

* <strong><code>doCache</code></strong>: True if caching should be enabled
* <strong><code>cacheSpec</code></strong>: Configuration for the cache, in [Guava CacheBuilderSpec format][10].
* <strong><code>extractDomainFromUrl</code></strong>: Are you inputting full URLs, like <code>http://www.google.com/mail</code>?  Then set this to true.  Otherwise, if you're inputting stripped-down domain or host names, such as <code>google.com</code>, then set it to false. This is used as a performance improvement at the caching level, so we don't have to look up the same domain over and over again just because the URL is different.

Usage
----------

This module defines a new function called <code>siterank()</code>.  

The function takes in a string (either a full URL or a domain/host - see above) 
and outputs the **reciprocal rank** of the site, which is a double between 0.0 and 1.0. 
0.0 is returned if the site is not found in the ranking.

The reciprocal rank is simply:

<code>1.0 / rank</code>

...so e.g. Google will probably have a reciprocal rank of 1.0 (1.0 / 1.0), WebMD might have 0.00239234 (1/0 / 418) and MyCoolHipsterSiteNobodyKnowsAbout.com 
might have of 0.0000000198867735 (1.0 / 50284678).

Most likely you will want to wrap this function in something like <code>exp()</code> to smooth the values, 
and to deal with cases where the function returns 0.0.  So the recommended usage is:

<code>exp(siterank(myUrlOrHostField))</code>

...which you can use as a boost function, e.g.

<code>http://mySite:8983/solr/select?q={!boost b=exp(siterank(myUrlOrHostField))}*:*</code>

So for instance, in the above examples, Google would have a score of 2.71828, WebMD would get 1.00239520844, and the hipster site would
get 1.00000001989.  Tweak the formula as you see fit.

See [my blog post on boosting][11] for more details about boosting in Solr.

Future work
----------

In the future, I'd like to expand this module to output rankings from other sources than Alexa, including custom config files.

Compile it yourself
----------

Download the code and do:

```
mvn install
```

[1]: http://www.apache.org/licenses/LICENSE-2.0.html
[2]: http://nolanlawson.com/2012/10/31/better-synonym-handling-in-solr
[3]: http://wiki.apache.org/solr/FunctionQuery
[4]: http://www.alexa.com/
[5]: http://infolab.stanford.edu/~backrub/google.html
[6]: http://www.hon.ch
[7]: http://nolanlawson.com
[8]: http://nolanlawson.s3.amazonaws.com/dist/org.healthonnet.lucene.siterank/release/1.1/hon-lucene-siterank-1.1.jar
[9]: http://search.maven.org/remotecontent?filepath=com/google/guava/guava/13.0.1/guava-13.0.1.jar
[10]: http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/cache/CacheBuilderSpec.html
[11]: http://nolanlawson.com/2012/06/02/comparing-boost-methods-in-solr/
