package com.ashish.redditclient.models;

/**
 * Created by User on 7/28/2015.
 */
public class SearchSubreddit {
    private String url;
    private String publicDescription;

    public String getPublicDescription() {
        return publicDescription;
    }

    public void setPublicDescription(String publicDescription) {
        this.publicDescription = publicDescription;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
