package com.example.iotproject

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.iotproject.fragments.gate.Gate
import com.example.iotproject.fragments.gate.GateDao
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException
import java.lang.Exception

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var gateDao: GateDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
                context, AppDatabase::class.java).build()
        gateDao = db.gateDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    suspend fun writeGate() {
        val gate: Gate = Gate("d8c0e668-b59e-455e-af78-77470ba291c5", "Cancello di Prova",
        "via delle strade rotte 13, Borgo Venezia, CA", null)
        gateDao.insert(gate)
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.iotproject", appContext.packageName)
    }
}