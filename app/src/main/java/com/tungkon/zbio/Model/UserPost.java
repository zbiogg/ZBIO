package com.tungkon.zbio.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPost {

    @SerializedName("postID")
    @Expose
    private Integer postID;
    @SerializedName("userID")
    @Expose
    private Integer userID;
    @SerializedName("post_Content")
    @Expose
    private Object postContent;
    @SerializedName("post_Image")
    @Expose
    private String postImage;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("userfullname")
    @Expose
    private String userfullname;
    @SerializedName("userAvt")
    @Expose
    private String userAvt;
    @SerializedName("like_qty")
    @Expose
    private Integer likeQty;
    @SerializedName("cmt_qty")
    @Expose
    private Integer cmtQty;

    public Integer getPostID() {
        return postID;
    }

    public void setPostID(Integer postID) {
        this.postID = postID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Object getPostContent() {
        return postContent;
    }

    public void setPostContent(Object postContent) {
        this.postContent = postContent;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserfullname() {
        return userfullname;
    }

    public void setUserfullname(String userfullname) {
        this.userfullname = userfullname;
    }

    public String getUserAvt() {
        return userAvt;
    }

    public void setUserAvt(String userAvt) {
        this.userAvt = userAvt;
    }

    public Integer getLikeQty() {
        return likeQty;
    }

    public void setLikeQty(Integer likeQty) {
        this.likeQty = likeQty;
    }

    public Integer getCmtQty() {
        return cmtQty;
    }

    public void setCmtQty(Integer cmtQty) {
        this.cmtQty = cmtQty;
    }

}