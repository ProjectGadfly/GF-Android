package com.forvm.gadfly.projectgadfly.data;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class getScriptIDResponse {

    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("id")
    @Expose
    @Nullable
    private Integer id;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Nullable
    public Integer getId() {
        return id;
    }

    public void setId(@Nullable Integer id) {
        this.id = id;
    }
}