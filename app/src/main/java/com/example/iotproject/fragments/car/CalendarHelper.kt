package com.example.iotproject.fragments.car

import android.widget.EditText
import com.example.iotproject.R
import java.text.SimpleDateFormat
import java.util.*

class CalendarHelper {
    /*
    private fun updateDateInView(): String {
        val dateFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.ITALY)
        //findViewById<EditText>(R.id.dateEditText).setText(sdf.format(calendar.time))
    }

    private fun updateTimeInView() {
        val timeFormat = "HH:mm"
        val sdf = SimpleDateFormat(timeFormat, Locale.ITALY)
        findViewById<EditText>(R.id.timeEditText).setText(sdf.format(calendar.time))
    }

    private fun checkDate(date: String): Boolean {
        val dateFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.ITALY)
        sdf.isLenient = false
        return try {
            sdf.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun checkTime(time: String): Boolean {
        val timeFormat = "HH:mm"
        val sdf = SimpleDateFormat(timeFormat, Locale.ITALY)
        sdf.isLenient = false
        return try {
            sdf.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getISO(date: String, time: String): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ITALY)
        val sdfISO = SimpleDateFormat("yyyy-MM-dd", Locale.ITALY)
        val convertedDate = sdfISO.format(sdf.parse(date)!!)
        return "$convertedDate $time:00"
    }

     */
}