package net.jamur2.jamur2nes;

import java.net.URL;
import java.util.Date;

public class Subscription {
    public String title;
    public java.net.URL url;
    public java.util.Date lastFetched;
    public String contents;

    public Subscription(String startTitle, java.net.URL startUrl,
            java.util.Date startLastFetched, String startContents) {
        title = startTitle;
        url = startUrl;
        lastFetched = startLastFetched;
        contents = startContents;
    }
}
