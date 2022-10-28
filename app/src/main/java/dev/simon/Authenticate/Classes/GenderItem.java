package dev.simon.Authenticate.Classes;

public class GenderItem {

    private String mGenderName;
    private int mGenderIcon;

    public GenderItem(String mGenderName, int mGenderIcon) {
        this.mGenderName = mGenderName;
        this.mGenderIcon = mGenderIcon;
    }

    public String getmGenderName() {
        return mGenderName;
    }

    public void setmGenderName(String mGenderName) {
        this.mGenderName = mGenderName;
    }

    public int getmGenderIcon() {
        return mGenderIcon;
    }

    public void setmGenderIcon(int mGenderIcon) {
        this.mGenderIcon = mGenderIcon;
    }
}
