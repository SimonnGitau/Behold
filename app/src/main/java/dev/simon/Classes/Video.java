package dev.simon.Classes;

public class Video {

    private String video_title, video_url, video_des, video_id, video_upload_time;
    private int video_views, video_likes, video_dislikes, video_cost;
    private boolean video_liked, video_purchased;

    public Video() {
    }

    public Video(String video_title, String video_url, String video_des, String video_id, String video_upload_time, int video_views, int video_likes, int video_dislikes, int video_cost, boolean video_liked, boolean video_purchased) {
        this.video_title = video_title;
        this.video_url = video_url;
        this.video_des = video_des;
        this.video_id = video_id;
        this.video_upload_time = video_upload_time;
        this.video_views = video_views;
        this.video_likes = video_likes;
        this.video_dislikes = video_dislikes;
        this.video_cost = video_cost;
        this.video_liked = video_liked;
        this.video_purchased = video_purchased;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getVideo_des() {
        return video_des;
    }

    public void setVideo_des(String video_des) {
        this.video_des = video_des;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_upload_time() {
        return video_upload_time;
    }

    public void setVideo_upload_time(String video_upload_time) {
        this.video_upload_time = video_upload_time;
    }

    public int getVideo_views() {
        return video_views;
    }

    public void setVideo_views(int video_views) {
        this.video_views = video_views;
    }

    public int getVideo_likes() {
        return video_likes;
    }

    public void setVideo_likes(int video_likes) {
        this.video_likes = video_likes;
    }

    public int getVideo_dislikes() {
        return video_dislikes;
    }

    public void setVideo_dislikes(int video_dislikes) {
        this.video_dislikes = video_dislikes;
    }

    public int getVideo_cost() {
        return video_cost;
    }

    public void setVideo_cost(int video_cost) {
        this.video_cost = video_cost;
    }

    public boolean isVideo_liked() {
        return video_liked;
    }

    public void setVideo_liked(boolean video_liked) {
        this.video_liked = video_liked;
    }

    public boolean isVideo_purchased() {
        return video_purchased;
    }

    public void setVideo_purchased(boolean video_purchased) {
        this.video_purchased = video_purchased;
    }
}
