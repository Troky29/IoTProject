package com.example.iotproject.database

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData


class AppRepository (private val gateDao: GateDao,
                     private val activityDao: ActivityDao,
                     private val carDao: CarDao) {

    val allGates: LiveData<List<Gate>> = gateDao.getAll()
    val allActivities: LiveData<List<Activity>> = activityDao.getAll()
    val allCars: LiveData<List<Car>> = carDao.getAll()

    @WorkerThread
    suspend fun insertGate(gate: Gate) {
        gateDao.insert(gate)
    }

    @WorkerThread
    suspend fun insertActivity(activity : Activity) {
        activityDao.insert(activity)
    }

    @WorkerThread
    suspend fun insertCar(car: Car) {
        carDao.insert(car)
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
    suspend fun insertAllCars(cars: List<Car>) {
        carDao.insertAll(cars)
    }

    @WorkerThread
    suspend fun deleteGate(gate: Gate) {
        gateDao.delete(gate)
    }

    @WorkerThread
    suspend fun deleteAllGates() {
        gateDao.deleteAll()
    }

    @WorkerThread
    suspend fun deleteActivity(activity: Activity) {
        activityDao.delete(activity)
    }

    @WorkerThread
    suspend fun deleteAllActivities() {
        activityDao.deleteAll()
    }

    @WorkerThread
    suspend fun deleteAllCars() {
        carDao.deleteAll()
    }

    @WorkerThread
    suspend fun deleteAll() {
        gateDao.deleteAll()
        carDao.deleteAll()
        activityDao.deleteAll()
    }
}