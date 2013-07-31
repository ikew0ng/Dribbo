
package com.refactech.driibo.vendor;

/**
 * Created by Issac on 7/18/13.
 */
public class DribbbleApi {
    private static final String BASE_URL = "http://api.dribbble.com";

    public static final String SHOTS_LIST = BASE_URL + "/shots/%1$s?page=%2$d";

    public static final String FOLLOWING = BASE_URL+"/players/%1$s/shots/following?page=%2$d";

    public static final String LIKES = BASE_URL+"/players/%1$s/shots/likes?page=%2$d";
}
