package com.example.hackathon;

import android.net.Uri;

import java.io.Serializable;

public class Report implements Serializable {
    public String reportId;
    public String userId;
    public String reportHash;
    public String description;
    public String dept;
    public String city;
    public boolean userStatus;
    public boolean adminStatus;
    public String uploadTime;
    public String uploader;

    public String getReportImageUrl() {
        return "https://ipfs.infura.io/ipfs/" + this.reportHash;
    }

    public Uri getReportImageUri() {
        Uri uri = Uri.parse(getReportImageUrl());
        return  uri;
    }
}
