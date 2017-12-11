package com.mjc.yhs.move2diner.DTO;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewListItem implements Comparable<ReviewListItem> {
    private String reviewTime;
    private String content;
    private String userNick;
    private String userId;
    private String thumbnail;
    private String truckName;
    private String truckUid;
    private String truckDes;
    private String truckThumbnail;
    private String key;

    //리뷰를 작성할때 현재시간, 내용, 사용자닉네임, 사용자uid,  사용자 썸네일, 트럭이름, 트럭정보, 트럭썸네일을 넣는다
    public ReviewListItem() {

    }

    public ReviewListItem(String reviewTime, String content, String userNick, String userId, String thumbnail, String truckName, String truckUid, String truckDes, String truckThumbnail) {
        this.reviewTime = reviewTime;
        this.content = content;
        this.userNick = userNick;
        this.userId = userId;
        this.thumbnail = thumbnail;
        this.truckName = truckName;
        this.truckUid = truckUid;
        this.truckDes = truckDes;
        this.truckThumbnail = truckThumbnail;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getTruckUid() {
        return truckUid;
    }

    public void setTruckUid(String truckUid) {
        this.truckUid = truckUid;
    }

    public String getTruckThumbnail() {
        return truckThumbnail;
    }

    public void setTruckThumbnail(String truckThumbnail) {
        this.truckThumbnail = truckThumbnail;
    }

    public String getTruckDes() {
        return truckDes;
    }

    public void setTruckDes(String truckDes) {
        this.truckDes = truckDes;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTruckName() {
        return truckName;
    }

    public void setTruckName(String truckName) {
        this.truckName = truckName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    //2017-10-12 11:15  vs 2017-10-31 10:55   이걸 비교해야함 => 내림차순
    @Override
    public int compareTo(@NonNull ReviewListItem o) {
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int compare = 1;
        try {
            Date date = sdfNow.parse(this.reviewTime);
            Date date2 = sdfNow.parse(o.reviewTime);
            compare = date.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return compare * -1;
    }
}
