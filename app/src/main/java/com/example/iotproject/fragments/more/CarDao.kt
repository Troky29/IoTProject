package com.example.iotproject.fragments.more

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.iotproject.database.BaseDao

@Dao
interface CarDao : BaseDao<Car> {
    @Query("DELETE FROM car")
    suspend fun deleteAll()

    @Query("SELECT * FROM car ORDER BY license ASC")
    fun getAll(): LiveData<List<Car>>
}