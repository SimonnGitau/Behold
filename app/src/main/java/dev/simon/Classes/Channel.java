package dev.simon.Classes;

public class Channel {

    private String chan_name, chan_id, chan_dp_url, chan_mail, church_name, church_loc;
    private int chan_mpesa_num, chan_acc_num;

    public Channel() {
        //Needed empty constructor
    }

    public Channel(String chan_name, String chan_id, String chan_dp_url, String chan_mail, String church_name, String church_loc, int chan_mpesa_num, int chan_acc_num) {
        this.chan_name = chan_name;
        this.chan_id = chan_id;
        this.chan_dp_url = chan_dp_url;
        this.chan_mail = chan_mail;
        this.church_name = church_name;
        this.church_loc = church_loc;
        this.chan_mpesa_num = chan_mpesa_num;
        this.chan_acc_num = chan_acc_num;
    }

    public String getChan_name() {
        return chan_name;
    }

    public void setChan_name(String chan_name) {
        this.chan_name = chan_name;
    }

    public String getChan_id() {
        return chan_id;
    }

    public void setChan_id(String chan_id) {
        this.chan_id = chan_id;
    }

    public String getChan_dp_url() {
        return chan_dp_url;
    }

    public void setChan_dp_url(String chan_dp_url) {
        this.chan_dp_url = chan_dp_url;
    }

    public String getChan_mail() {
        return chan_mail;
    }

    public void setChan_mail(String chan_mail) {
        this.chan_mail = chan_mail;
    }

    public String getChurch_name() {
        return church_name;
    }

    public void setChurch_name(String church_name) {
        this.church_name = church_name;
    }

    public String getChurch_loc() {
        return church_loc;
    }

    public void setChurch_loc(String church_loc) {
        this.church_loc = church_loc;
    }

    public int getChan_mpesa_num() {
        return chan_mpesa_num;
    }

    public void setChan_mpesa_num(int chan_mpesa_num) {
        this.chan_mpesa_num = chan_mpesa_num;
    }

    public int getChan_acc_num() {
        return chan_acc_num;
    }

    public void setChan_acc_num(int chan_acc_num) {
        this.chan_acc_num = chan_acc_num;
    }
}
