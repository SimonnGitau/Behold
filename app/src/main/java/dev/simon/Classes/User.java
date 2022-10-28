package dev.simon.Classes;

public class User {

    private String user_name, user_id, user_mail, user_pass, user_phone, user_country, user_dp_url, user_gender, user_channel_id;
    private boolean user_phone_verified, user_channel;
    private int user_following;

    public User() {
        //Needed empty constructor
    }

    public User(String user_name, String user_id, String user_mail, String user_pass, String user_phone, String user_country, String user_dp_url, String user_gender, String user_channel_id, boolean user_phone_verified, boolean user_channel, int user_following) {
        this.user_name = user_name;
        this.user_id = user_id;
        this.user_mail = user_mail;
        this.user_pass = user_pass;
        this.user_phone = user_phone;
        this.user_country = user_country;
        this.user_dp_url = user_dp_url;
        this.user_gender = user_gender;
        this.user_channel_id = user_channel_id;
        this.user_phone_verified = user_phone_verified;
        this.user_channel = user_channel;
        this.user_following = user_following;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_mail() {
        return user_mail;
    }

    public void setUser_mail(String user_mail) {
        this.user_mail = user_mail;
    }

    public String getUser_pass() {
        return user_pass;
    }

    public void setUser_pass(String user_pass) {
        this.user_pass = user_pass;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_country() {
        return user_country;
    }

    public void setUser_country(String user_country) {
        this.user_country = user_country;
    }

    public String getUser_dp_url() {
        return user_dp_url;
    }

    public void setUser_dp_url(String user_dp_url) {
        this.user_dp_url = user_dp_url;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getUser_channel_id() {
        return user_channel_id;
    }

    public void setUser_channel_id(String user_channel_id) {
        this.user_channel_id = user_channel_id;
    }

    public boolean isUser_phone_verified() {
        return user_phone_verified;
    }

    public void setUser_phone_verified(boolean user_phone_verified) {
        this.user_phone_verified = user_phone_verified;
    }

    public boolean isUser_channel() {
        return user_channel;
    }

    public void setUser_channel(boolean user_channel) {
        this.user_channel = user_channel;
    }

    public int getUser_following() {
        return user_following;
    }

    public void setUser_following(int user_following) {
        this.user_following = user_following;
    }
}
