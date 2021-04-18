package com.example.iotproject.fragments.more

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(car: Car)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cars: List<Car>)

    @Update
    fun update(car: Car)

    @Query("DELETE FROM car")
    suspend fun deleteAll()

    @Query("SELECT * FROM car ORDER BY license ASC")
    fun getAll(): LiveData<List<Car>>
}