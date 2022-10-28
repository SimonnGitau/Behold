package dev.simon.Classes;

public class Event {

    private String event_title, event_venue, event_des, event_id;
    private String event_time;
    private String event_date;
    private int event_int = 0;
    private int event_com = 0;
    private String event_img_uri;
    private boolean event_interested;

    public Event() {
        //Needed empty constructor
    }

    public Event(String event_title, String event_venue, String event_des, String event_id, String event_time, String event_date, int event_int, int event_com, String event_img_uri, boolean event_interested) {
        this.event_title = event_title;
        this.event_venue = event_venue;
        this.event_des = event_des;
        this.event_id = event_id;
        this.event_time = event_time;
        this.event_date = event_date;
        this.event_int = event_int;
        this.event_com = event_com;
        this.event_img_uri = event_img_uri;
        this.event_interested = event_interested;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public String getEvent_venue() {
        return event_venue;
    }

    public void setEvent_venue(String event_venue) {
        this.event_venue = event_venue;
    }

    public String getEvent_des() {
        return event_des;
    }

    public void setEvent_des(String event_des) {
        this.event_des = event_des;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public int getEvent_int() {
        return event_int;
    }

    public void setEvent_int(int event_int) {
        this.event_int = event_int;
    }

    public int getEvent_com() {
        return event_com;
    }

    public void setEvent_com(int event_com) {
        this.event_com = event_com;
    }

    public String getEvent_img_uri() {
        return event_img_uri;
    }

    public void setEvent_img_uri(String event_img_uri) {
        this.event_img_uri = event_img_uri;
    }

    public boolean isEvent_interested() {
        return event_interested;
    }

    public void setEvent_interested(boolean event_interested) {
        this.event_interested = event_interested;
    }
}
