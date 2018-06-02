package com.forvm.gadfly.projectgadfly.data;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class getRepsResponse {

    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Results")
    @Expose
    @Nullable
    private List<Representative> representatives = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Representative> getRepresentatives() {
        return representatives;
    }

    public void setRepresentatives(List<Representative> representatives) {
        this.representatives = representatives;
    }
}