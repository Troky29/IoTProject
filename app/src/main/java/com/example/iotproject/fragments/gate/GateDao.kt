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
    fun delete(gate: Gate)

    @Query("DELETE FROM gate")
    fun deleteAll()

    @Query("SELECT * FROM gate ORDER BY name DESC")
    fun getAll(): LiveData<List<Gate>>
}