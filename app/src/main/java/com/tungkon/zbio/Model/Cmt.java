package com.tungkon.zbio.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cmt {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("postID")
@Expose
private Integer postID;
@SerializedName("userID")
@Expose
private Integer userID;
@SerializedName("content_cmt")
@Expose
private String contentCmt;
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
@SerializedName("repcmt_qty")
@Expose
private Integer repcmtQty;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

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

public String getContentCmt() {
return contentCmt;
}

public void setContentCmt(String contentCmt) {
this.contentCmt = contentCmt;
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

public Integer getRepcmtQty() {
return repcmtQty;
}

public void setRepcmtQty(Integer repcmtQty) {
this.repcmtQty = repcmtQty;
}

    @Override
    public String toString() {
        return "Cmt{" +
                "id=" + id +
                ", postID=" + postID +
                ", userID=" + userID +
                ", contentCmt='" + contentCmt + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", userfullname='" + userfullname + '\'' +
                ", userAvt='" + userAvt + '\'' +
                ", repcmtQty=" + repcmtQty +
                '}';
    }
}