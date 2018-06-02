package com.forvm.gadfly.projectgadfly.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class Ticket {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("ticket")
    @Expose
    private String ticket;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}
