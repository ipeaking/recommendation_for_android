package com.bo.bonews.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsBean implements Parcelable {

    private String title;
    private String time;
    private int type;
    private String stype;
    private String content;
    private String contentId;


    public NewsBean() {
    }

    public NewsBean(String title,String time) {
        this.title = title;
        this.time = time;
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.time);
        dest.writeInt(this.type);
        dest.writeString(this.stype);
        dest.writeString(this.content);
        dest.writeString(this.contentId);
    }

    protected NewsBean(Parcel in) {
        this.title = in.readString();
        this.time = in.readString();
        this.type = in.readInt();
        this.stype = in.readString();
        this.content = in.readString();
        this.contentId = in.readString();
    }

    public static final Parcelable.Creator<NewsBean> CREATOR = new Parcelable.Creator<NewsBean>() {
        @Override
        public NewsBean createFromParcel(Parcel source) {
            return new NewsBean(source);
        }

        @Override
        public NewsBean[] newArray(int size) {
            return new NewsBean[size];
        }
    };
}
