package com.forvm.gadfly.projectgadfly.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RepresentativeDAO {
    @Query("SELECT * FROM Representative")
    List<Representative> getAll();

    @Insert
    long insertRep(Representative representative);

    @Delete
    void delete(Representative representative);

    @Update
    void update(Representative representative);

}
