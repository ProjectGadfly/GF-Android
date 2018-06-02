package com.forvm.gadfly.projectgadfly.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
@Dao
public interface TicketDAO {
    @Query("SELECT * FROM Ticket")
    List<Ticket> getAll();

    @Insert
    long insertTicket(Ticket ticket);

    @Delete
    void delete(Ticket ticket);

    @Update
    void update(Ticket ticket);

}
