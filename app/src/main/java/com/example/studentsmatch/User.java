package com.example.studentsmatch;

import java.io.Serializable;

public class User implements Serializable {
    private String userName;
    private String userEmail;
    private String userYear;
    private String userPhone;
    private String userPass;
    private String pic;

    public User(){

    }

    public User(String userName, String userEmail, String userYear, String userPhone, String userPass, String pic) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userYear = userYear;
        this.userPhone = userPhone;
        this.userPass = userPass;
        this.pic=pic;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserYear() {
        return userYear;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserYear(String userYear) {
        this.userYear = userYear;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userYear='" + userYear + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", pic='" + pic + '\'' +
                '}';
    }
}
