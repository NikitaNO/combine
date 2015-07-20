package google;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Ni on 20.11.2014.
 */
//TODO port to scala
public class GoogleLinkShorter {
    static final private String APIKEY = "AIzaSyB6AHtz9HK-8Vag93TkxG-AfpJsSnoRBn4";
    static private String googUrl = "https://www.googleapis.com/urlshortener/v1/url?shortUrl=http://goo.gl/fbsS&key=" + APIKEY;

    static public String shorten(String longUrl)
    {
        String shortUrl = "";
        try
        {
            URLConnection conn = new URL(googUrl).openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter wr =
                    new OutputStreamWriter(conn.getOutputStream());
            wr.write("{\"longUrl\":\"" + longUrl + "\"}");
            wr.flush();

            // Get the response
            BufferedReader rd =
                    new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = rd.readLine()) != null)
            {
                if (line.indexOf("id") > -1)
                {
                    // I'm sure there's a more elegant way of parsing
                    // the JSON response, but this is quick/dirty =)
                    shortUrl = line.substring(8, line.length() - 2);
                    break;
                }
            }

            wr.close();
            rd.close();
        }
        catch (MalformedURLException ex)
        {
            Logger.getLogger(GoogleLinkShorter.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        catch (IOException ex)
        {
            Logger.getLogger(GoogleLinkShorter.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        return shortUrl;
    }

}
