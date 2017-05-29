package com.benjamin.ledet.budget.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private long id;

    private String email;

    private String givenName;

    private String familyName;

    private String photoUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDisplayName(){
        return givenName + " " + familyName;
    }

    @Override
    public String toString() {
        return  "id : " + id + " - " +
                "email : " + email + " - " +
                "given name : " + givenName + " - " +
                "family name : " + familyName + " - " +
                "photo url : " + photoUrl;
    }
}
