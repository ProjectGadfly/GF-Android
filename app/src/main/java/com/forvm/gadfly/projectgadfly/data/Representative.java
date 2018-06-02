package com.forvm.gadfly.projectgadfly.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
@Entity
public class Representative {

    @PrimaryKey(autoGenerate = true)
    private long repID;


    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("picURL")
    @Expose
    private String picURL;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("party")
    @Expose
    private String party;
    @SerializedName("tags")
    @Expose
    @TypeConverters(ListConverter.class)
    private List<Integer> tags = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public long getRepID() {
        return repID;
    }

    public void setRepID(long repID) {
        this.repID = repID;
    }

    static class ListConverter {
        @TypeConverter
        public List<Integer> storedStringToIDs(String value) {
            List<Integer> tags = new ArrayList<>();
            for (String s : value.split("\\s*,\\s*")) {
                tags.add(Integer.parseInt(s));
            }
            return tags;
        }

        @TypeConverter
        public String tagsToStoredString(List<Integer> tags) {
            String value = "";
            for (Integer tag : tags)
                value += tag + ",";
            return value;
        }
    }
}
