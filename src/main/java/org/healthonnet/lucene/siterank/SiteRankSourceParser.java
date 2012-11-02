package org.healthonnet.lucene.siterank;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.ValueSourceParser;
import org.apache.solr.search.function.DocValues;
import org.apache.solr.search.function.ValueSource;

public class SiteRankSourceParser extends ValueSourceParser {

    @Override
    public ValueSource parse(FunctionQParser functionQParser) throws ParseException {
        ValueSource source = functionQParser.parseValueSource();
        String val = functionQParser.parseArg();
        return new SiteRankFunction(source, val);
    }
    
    private static class SiteRankFunction extends ValueSource {

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
                    return (short)intVal(doc);
                }

                @Override
                public float floatVal(int doc) {
                    return (float)intVal(doc);
                }

                @Override
                public int intVal(int doc) {
                    String url = docValues.strVal(doc);
                    return SiteRankHelper.getRank(url);
                }

                @Override
                public long longVal(int doc) {
                    return (long)intVal(doc);
                }

                @Override
                public double doubleVal(int doc) {
                    return (double)intVal(doc);
                }

                @Override
                public String strVal(int doc) {
                    return String.valueOf(intVal(doc));
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
            return "SiteRank (siterank) function";
        }
    }
}
