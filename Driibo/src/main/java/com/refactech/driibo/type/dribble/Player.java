
package com.refactech.driibo.type.dribble;

/**
 * Created by Issac on 7/18/13.
 */
public class Player {
    private long id;

    private String name;

    private String url;

    private String avatar_url;

    private String location;

    private String twitter_screen_name;

    private String drafted_by_player_id;

    private int shots_count;

    private int draftees_count;

    private int followers_count;

    private int following_count;

    private int comments_count;

    private int comments_received_count;

    private int likes_count;

    private int likes_received_count;

    private int rebounds_count;

    private int rebounds_received_count;

    private String created_at;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getLocation() {
        return location;
    }

    public String getTwitter_screen_name() {
        return twitter_screen_name;
    }

    public String getDrafted_by_player_id() {
        return drafted_by_player_id;
    }

    public int getShots_count() {
        return shots_count;
    }

    public int getDraftees_count() {
        return draftees_count;
    }

    public int getFollowers_count() {
        return followers_count;
    }

    public int getFollowing_count() {
        return following_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public int getComments_received_count() {
        return comments_received_count;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public int getLikes_received_count() {
        return likes_received_count;
    }

    public int getRebounds_count() {
        return rebounds_count;
    }

    public int getRebounds_received_count() {
        return rebounds_received_count;
    }

    public String getCreated_at() {
        return created_at;
    }
}
