package dev.simon.Classes;

public class VidYou {

    private String you_vid_id, you_id, you_vid_des, you_vid_upload_time;

    public VidYou() {
        //Needed empty constructor
    }

    public VidYou(String you_vid_id, String you_id, String you_vid_des, String you_vid_upload_time) {
        this.you_vid_id = you_vid_id;
        this.you_id = you_id;
        this.you_vid_des = you_vid_des;
        this.you_vid_upload_time = you_vid_upload_time;
    }

    public String getYou_vid_id() {
        return you_vid_id;
    }

    public void setYou_vid_id(String you_vid_id) {
        this.you_vid_id = you_vid_id;
    }

    public String getYou_id() {
        return you_id;
    }

    public void setYou_id(String you_id) {
        this.you_id = you_id;
    }

    public String getYou_vid_des() {
        return you_vid_des;
    }

    public void setYou_vid_des(String you_vid_des) {
        this.you_vid_des = you_vid_des;
    }

    public String getYou_vid_upload_time() {
        return you_vid_upload_time;
    }

    public void setYou_vid_upload_time(String you_vid_upload_time) {
        this.you_vid_upload_time = you_vid_upload_time;
    }
}
