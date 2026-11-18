package com.example.studentsmatch;

import android.net.Uri;

import java.io.Serializable;

public class Request  implements Serializable {

    private String requestname;
    private String requestsubject;
    private String requestdescription;
    private String requestimage;

    public Request(String requestname, String requestsubject, String requestdescription, String requestimage) {
        this.requestname = requestname;
        this.requestsubject = requestsubject;
        this.requestdescription = requestdescription;
        this.requestimage = requestimage;

    }

    public String getRequestname() {
        return requestname;
    }

    public void setRequestname(String requestname) {
        this.requestname = requestname;
    }

    public String getRequestsubject() {
        return requestsubject;
    }

    public void setRequestsubject(String requestsubject) {
        this.requestsubject = requestsubject;
    }

    public String getRequestdescription() {
        return requestdescription;
    }

    public void setRequestdescription(String requestdescription) {
        this.requestdescription = requestdescription;
    }

    public String getRequestimage() {
        return requestimage;
    }

    public void setRequestimage(String requestimage) {
        this.requestimage = requestimage;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestemail='" + requestname + '\'' +
                ", requestsubject='" + requestsubject + '\'' +
                ", requestdescription='" + requestdescription + '\'' +
                ", requestimage='" + requestimage + '\'' +
                '}';
    }
}

