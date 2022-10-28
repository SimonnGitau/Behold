package dev.simon.Classes;

public class Teach {

    private String teach_topic, teach_img, teach_sub, teach_verses, teach_des, teach_id;
    private boolean teach_liked = false;
    private int teach_likes;

    public Teach() {
        //Needed empty constructor
    }

    public Teach(String teach_topic, String teach_img, String teach_sub, String teach_verses, String teach_des, String teach_id, boolean teach_liked, int teach_likes) {
        this.teach_topic = teach_topic;
        this.teach_img = teach_img;
        this.teach_sub = teach_sub;
        this.teach_verses = teach_verses;
        this.teach_des = teach_des;
        this.teach_id = teach_id;
        this.teach_liked = teach_liked;
        this.teach_likes = teach_likes;
    }

    public String getTeach_topic() {
        return teach_topic;
    }

    public void setTeach_topic(String teach_topic) {
        this.teach_topic = teach_topic;
    }

    public String getTeach_img() {
        return teach_img;
    }

    public void setTeach_img(String teach_img) {
        this.teach_img = teach_img;
    }

    public String getTeach_sub() {
        return teach_sub;
    }

    public void setTeach_sub(String teach_sub) {
        this.teach_sub = teach_sub;
    }

    public String getTeach_verses() {
        return teach_verses;
    }

    public void setTeach_verses(String teach_verses) {
        this.teach_verses = teach_verses;
    }

    public String getTeach_des() {
        return teach_des;
    }

    public void setTeach_des(String teach_des) {
        this.teach_des = teach_des;
    }

    public String getTeach_id() {
        return teach_id;
    }

    public void setTeach_id(String teach_id) {
        this.teach_id = teach_id;
    }

    public boolean isTeach_liked() {
        return teach_liked;
    }

    public void setTeach_liked(boolean teach_liked) {
        this.teach_liked = teach_liked;
    }

    public int getTeach_likes() {
        return teach_likes;
    }

    public void setTeach_likes(int teach_likes) {
        this.teach_likes = teach_likes;
    }
}
