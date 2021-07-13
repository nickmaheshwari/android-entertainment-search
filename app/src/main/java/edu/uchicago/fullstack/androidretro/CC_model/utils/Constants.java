package edu.uchicago.fullstack.androidretro.CC_model.utils;

public class Constants {
    //these constants are used as keys throughout the 3-tier architecture

    public static String title = "TITLE";
    public static String imdbId = "IMDB_ID";
    public static String year = "YEAR";
    public static String type = "TYPE";
    public static String imageUrl = "IMAGE_URL";
    public static String keyword = "KEYWORD";
    public static String address = "ADDRESS";
    public static String news_feed_myPref = "NewFeedMyPref";

    public static final String default_keyword = "comedy";

    //for invalidating the cache
    public static long time_to_stale_millis = 30_000;
}
