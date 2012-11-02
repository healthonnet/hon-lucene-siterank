package org.healthonnet.lucene.siterank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class SiteRankHelper {

    // undocumented Alexa site rank api
    private static final String BASE_URL = "http://data.alexa.com/data";
    private static final Pattern SITE_RANK_PATTERN = Pattern.compile("RANK=\"(\\d+)\"");
    private static final int DEFAULT_VALUE = -1;

    public static int getRank(String url) {
        HttpGet httpGet = new HttpGet("http://www.vogella.com");
        HttpMethod method = new GetMethod(BASE_URL);
        method.setQueryString(new NameValuePair[] { new NameValuePair("cli", "10"), new NameValuePair("url", url)

        });

        HttpGet request = new HttpGet(httpGet.getURI().toString());

        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = client.execute(request);
        } catch (ClientProtocolException e1) {
            e1.printStackTrace();
            return DEFAULT_VALUE;
        } catch (IOException e1) {
            e1.printStackTrace();
            return DEFAULT_VALUE;
        }
        
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                Matcher matcher = SITE_RANK_PATTERN.matcher(line);
                if (matcher.find()) {
                    return Integer.valueOf(matcher.group(1));
                }
            }
        } catch (IOException e) {
            // ignore
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // ignore
                    e.printStackTrace();
                }
            }
        }

        return DEFAULT_VALUE;
    }

}
