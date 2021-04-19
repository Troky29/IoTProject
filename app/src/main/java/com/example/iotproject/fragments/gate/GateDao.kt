package com.example.iotproject.fragments.gate

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.iotproject.database.BaseDao

@Dao
interface GateDao : BaseDao<Gate> {
    @Query("DELETE FROM gate")
    suspend fun deleteAll()

    @Query("SELECT * FROM gate ORDER BY name ASC")
    fun getAll(): LiveData<List<Gate>>
}