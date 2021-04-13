package com.example.iotproject.fragments.activity

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: Activity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activity: List<Activity>)

    @Update
    suspend fun update(activity: Activity)

    @Query("DELETE FROM activity")
    suspend fun deleteAll()

    @Query("SELECT * FROM activity ORDER BY datetime DESC")
    fun getAll(): LiveData<List<Activity>>

    //TODO: see if we need more query
}