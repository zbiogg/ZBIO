package com.tungkon.zbio.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("postID")
    @Expose
    private Integer postID;
    @SerializedName("userID")
    @Expose
    private Integer userID;
    @SerializedName("post_Content")
    @Expose
    private String postContent;
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
    @SerializedName("liked")
    @Expose
    private Integer liked;

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

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
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
    public Integer getLiked() {
        return liked;
    }

    public void setLiked(Integer liked) {
        this.liked = liked;
    }

    @Override
    public String toString() {
        return "Data{" +
                "postID=" + postID +
                ", userID=" + userID +
                ", postContent='" + postContent + '\'' +
                ", postImage=" + postImage +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", userfullname='" + userfullname + '\'' +
                ", userAvt='" + userAvt + '\'' +
                ", likeQty=" + likeQty +
                ", cmtQty=" + cmtQty +
                ", liked=" + liked +
                '}';
    }
}