package com.example.iotproject.fragments.car

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.iotproject.LoadingDialog
import com.example.iotproject.R
import java.text.SimpleDateFormat
import java.util.*

class SpecialRuleDialog : DialogFragment() {
    private val calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_special_rule, container, false)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        view.findViewById<ImageButton>(R.id.calendarImageButton).setOnClickListener() {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                DatePickerDialog(requireContext(),
                        dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        }

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            updateTimeInView()
        }
        view.findViewById<ImageButton>(R.id.timeImageButton).setOnClickListener {
            TimePickerDialog(requireContext(),
                    timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true).show()
        }

        view.findViewById<Button>(R.id.confirmButton).setOnClickListener {
            val nickname = view.findViewById<TextView>(R.id.nicknameEditText).text.toString()
            val date = view.findViewById<EditText>(R.id.dateEditText).text.toString()
            val time = view.findViewById<EditText>(R.id.timeEditText).text.toString()

            val correctedDate = date.trim().replace("/", "-")
            val correctedTime = time.trim().replace(".", ":")

            if (nickname.isEmpty()) {
                messenger("Please insert a nickname")
                return@setOnClickListener
            }
            //TODO: check for actual date and time format
            if(correctedDate.isEmpty() || !checkDate(correctedDate)) {
                messenger("Insert a valid date (day-month-year)")
                return@setOnClickListener
            }
            if (correctedTime.isEmpty() || !checkTime(correctedTime)) {
                messenger("Insert a valid time (hour:minute)")
                return@setOnClickListener
            }

            val dateTime = getISO(date, time)
            //LoadingDialog().show()
            //TODO: pass the information about the special rule
        }
        return view
    }

    private fun updateDateInView() {
        val dateFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        view?.findViewById<EditText>(R.id.dateEditText)?.setText(sdf.format(calendar.time))
    }

    private fun updateTimeInView() {
        val timeFormat = "HH:mm"
        val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
        view?.findViewById<EditText>(R.id.timeEditText)?.setText(sdf.format(calendar.time))
    }

    private fun checkDate(date: String): Boolean {
        val dateFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
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
        val sdf = SimpleDateFormat(timeFormat, Locale.getDefault())
        sdf.isLenient = false
        return try {
            sdf.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun getISO(date: String, time: String): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val sdfISO = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val convertedDate = sdfISO.format(sdf.parse(date)!!)
        return "$convertedDate $time:00"
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.75).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun messenger(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}