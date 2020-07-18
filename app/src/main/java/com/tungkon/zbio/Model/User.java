package com.tungkon.zbio.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("username")
@Expose
private String username;
@SerializedName("email")
@Expose
private String email;
@SerializedName("firstName")
@Expose
private String firstName;
@SerializedName("lastName")
@Expose
private String lastName;
@SerializedName("gender")
@Expose
private String gender;
@SerializedName("doB")
@Expose
private String doB;
@SerializedName("phone")
@Expose
private String phone;
@SerializedName("city")
@Expose
private Object city;
@SerializedName("about")
@Expose
private Object about;
@SerializedName("img_avt")
@Expose
private String imgAvt;
@SerializedName("img_cover")
@Expose
private Object imgCover;
@SerializedName("fbID")
@Expose
private Object fbID;
@SerializedName("created_at")
@Expose
private Object createdAt;
@SerializedName("updated_at")
@Expose
private String updatedAt;
@SerializedName("status")
@Expose
private Integer status;
@SerializedName("auth")
@Expose
private Integer auth;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public String getUsername() {
return username;
}

public void setUsername(String username) {
this.username = username;
}

public String getEmail() {
return email;
}

public void setEmail(String email) {
this.email = email;
}

public String getFirstName() {
return firstName;
}

public void setFirstName(String firstName) {
this.firstName = firstName;
}

public String getLastName() {
return lastName;
}

public void setLastName(String lastName) {
this.lastName = lastName;
}

public String getGender() {
return gender;
}

public void setGender(String gender) {
this.gender = gender;
}

public String getDoB() {
return doB;
}

public void setDoB(String doB) {
this.doB = doB;
}

public String getPhone() {
return phone;
}

public void setPhone(String phone) {
this.phone = phone;
}

public Object getCity() {
return city;
}

public void setCity(Object city) {
this.city = city;
}

public Object getAbout() {
return about;
}

public void setAbout(Object about) {
this.about = about;
}

public String getImgAvt() {
return imgAvt;
}

public void setImgAvt(String imgAvt) {
this.imgAvt = imgAvt;
}

public Object getImgCover() {
return imgCover;
}

public void setImgCover(Object imgCover) {
this.imgCover = imgCover;
}

public Object getFbID() {
return fbID;
}

public void setFbID(Object fbID) {
this.fbID = fbID;
}

public Object getCreatedAt() {
return createdAt;
}

public void setCreatedAt(Object createdAt) {
this.createdAt = createdAt;
}

public String getUpdatedAt() {
return updatedAt;
}

public void setUpdatedAt(String updatedAt) {
this.updatedAt = updatedAt;
}

public Integer getStatus() {
return status;
}

public void setStatus(Integer status) {
this.status = status;
}

public Integer getAuth() {
return auth;
}

public void setAuth(Integer auth) {
this.auth = auth;
}

}