package com.cs371m.ads.karma_farm;

/**
 * Created by stipton on 10/25/14.
 */
public class KarmaFarmSubmission {

    public String _id;  //
    public String title;
    public String link;
    public int karma;
    public int image;

    public KarmaFarmSubmission() {
        super();
    }

    public KarmaFarmSubmission(String _id, String title, String link, int karma, int image) {
        super();

        this._id = _id;
        this.title = title;
        this.link = link;
        this.karma = karma;
        this.image = image;
    }



}
