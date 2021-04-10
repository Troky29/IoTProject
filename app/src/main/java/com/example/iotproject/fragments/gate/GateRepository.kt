package com.example.iotproject.fragments.gate

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData


class GateRepository (private val gateDao: GateDao) {
    val allGates: LiveData<List<Gate>> = gateDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(gate: Gate) {
        gateDao.insert(gate)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(gates: List<Gate>) {
        gateDao.insertAll(gates)
    }
}