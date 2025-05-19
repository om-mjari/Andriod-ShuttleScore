package com.example.shuttlescore;

import java.security.Timestamp;

public class tbl_Users {
    private String userId;
    private String name;
    private String address;
    private String contactNo;
    private String emailId;
    private String gender;
    private String handedness;
    private long dateOfBirth,createdAt,deletedAt;
    public tbl_Users(){

    }

    public tbl_Users(String userId,String name, String address, String contactNo, String emailId,
                     String gender, String handedness, long dateOfBirth, long createdAt, long deletedAt) {
        this.userId = userId;
        this.name = name;
        this.address = address;
        this.contactNo = contactNo;
        this.emailId = emailId;
        this.gender = gender;
        this.handedness = handedness;
        this.dateOfBirth = dateOfBirth;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getGender() {
        return gender;
    }

    public String getHandedness() {
        return handedness;
    }
    public long getDateOfBirth() {
        return dateOfBirth;
    }
    public long getCreatedAt() {
        return createdAt;
    }
    public long getDeletedAt(){
        return deletedAt;
    }
}
