package com.example.iotproject

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.iotproject.fragments.activity.Activity
import com.example.iotproject.fragments.activity.ActivityDao
import com.example.iotproject.fragments.gate.Gate
import com.example.iotproject.fragments.gate.GateDao


class AppRepository (private val gateDao: GateDao, private val activityDao: ActivityDao) {
    val allGates: LiveData<List<Gate>> = gateDao.getAll()
    val allActivities: LiveData<List<Activity>> = activityDao.getAll()

    @WorkerThread
    suspend fun insertGate(gate: Gate) {
        gateDao.insert(gate)
    }

    @WorkerThread
    suspend fun insertActivity(activity : Activity) {
        activityDao.insert(activity)
    }

    @WorkerThread
    suspend fun updateActivity(activity: Activity) {
        activityDao.update(activity)
    }

    @WorkerThread
    suspend fun updateAllActivities(activities: List<Activity>) {
        activityDao.insertAll(activities)
    }

    @WorkerThread
    suspend fun insertAllGates(gates: List<Gate>) {
        gateDao.insertAll(gates)
    }

    @WorkerThread
    suspend fun insertAllActivities(activities: List<Activity>) {
        activityDao.insertAll(activities)
    }

    @WorkerThread
    suspend fun deleteAllGates() {
        gateDao.deleteAll()
    }

    @WorkerThread
    suspend fun deleteAllActivities() {
        activityDao.deleteAll()
    }
}