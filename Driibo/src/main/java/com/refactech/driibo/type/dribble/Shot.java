
package com.refactech.driibo.type.dribble;

import java.util.ArrayList;

/**
 * Created by Issac on 7/18/13.
 */
public class Shot {
    private long id;

    private String title;

    private String url;

    private String short_url;

    private String image_url;

    private String image_teaser_url;

    private int width;

    private int height;

    private int views_count;

    private int likes_count;

    private int comments_count;

    private int rebounds_count;

    private long rebound_source_id;

    private String created_at;

    private Player player;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getShort_url() {
        return short_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getImage_teaser_url() {
        return image_teaser_url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getViews_count() {
        return views_count;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public int getRebounds_count() {
        return rebounds_count;
    }

    public long getRebound_source_id() {
        return rebound_source_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Player getPlayer() {
        return player;
    }

    public static class ShotsRequestData {
        private int page;

        private int pages;

        private int per_page;

        private int total;

        private ArrayList<Shot> shots;

        public int getPage() {
            return page;
        }

        public int getPages() {
            return pages;
        }

        public int getPer_page() {
            return per_page;
        }

        public int getTotal() {
            return total;
        }

        public ArrayList<Shot> getShots() {
            return shots;
        }
    }
}
