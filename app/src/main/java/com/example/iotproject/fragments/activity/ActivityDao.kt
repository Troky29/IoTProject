package com.example.iotproject.fragments.activity

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.iotproject.database.BaseDao

@Dao
interface ActivityDao : BaseDao<Activity> {
    @Query("DELETE FROM activity")
    suspend fun deleteAll()

    @Query("SELECT * FROM activity ORDER BY datetime DESC")
    fun getAll(): LiveData<List<Activity>>
}