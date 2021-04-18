package com.example.iotproject.fragments.gate

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gate: Gate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(gates: List<Gate>)

    @Update
    fun update(gate: Gate)

    @Delete
    suspend fun delete(gate: Gate)

    @Query("DELETE FROM gate")
    suspend fun deleteAll()

    @Query("SELECT * FROM gate ORDER BY name ASC")
    fun getAll(): LiveData<List<Gate>>
}