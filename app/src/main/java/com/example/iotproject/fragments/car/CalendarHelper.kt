package com.example.iotproject.fragments.car

import java.text.SimpleDateFormat
import java.util.*

class CalendarHelper {
    private val dateFormat = "dd-MM-yyy"
    private val timeFormat = "HH:mm"
    private val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
    private val simpleTimeFormat = SimpleDateFormat(timeFormat, Locale.getDefault())


    fun getFormattedDate(calendar: Calendar): String {
        return simpleDateFormat.format(calendar.time)
    }

    fun getFormattedTime(calendar: Calendar): String {
        return simpleTimeFormat.format(calendar.time)
    }

    fun checkDate(date: String): Boolean {
        simpleDateFormat.isLenient = false
        return try {
            simpleDateFormat.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun checkTime(time: String): Boolean {
        simpleTimeFormat.isLenient = false
        return try {
            simpleTimeFormat.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getISO(date: String, time: String): String {
        val sdfISO = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val convertedDate = sdfISO.format(simpleDateFormat.parse(date)!!)
        return "$convertedDate $time:00"
    }

    fun isAfterNow(dateTime: String): Boolean {
        val sdfISO = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDateTime = sdfISO.format(Calendar.getInstance().time)
        return dateTime > currentDateTime
    }
}