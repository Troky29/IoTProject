package com.example.iotproject.fragments.car

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.iotproject.R
import java.util.*

class SpecialRuleDialog : DialogFragment() {
    private val calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_special_rule, container, false)

        val calendarHelper = CalendarHelper()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            view.findViewById<EditText>(R.id.dateEditText).setText(calendarHelper.getFormattedDate(calendar))
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
            view.findViewById<EditText>(R.id.timeEditText).setText(calendarHelper.getFormattedTime(calendar))
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
            if(correctedDate.isEmpty() || !calendarHelper.checkDate(correctedDate)) {
                messenger("Insert a valid date (day-month-year)")
                return@setOnClickListener
            }
            if (correctedTime.isEmpty() || !calendarHelper.checkTime(correctedTime)) {
                messenger("Insert a valid time (hour:minute)")
                return@setOnClickListener
            }

            val dateTime = calendarHelper.getISO(date, time)
            if (calendarHelper.isAfterNow(dateTime)) {
                sendResult(nickname, dateTime)
            } else {
                messenger("The date inserted is in the past")
                return@setOnClickListener
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.75).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun sendResult(nickname: String, dateTime: String) {
        val intent = Intent().apply {
            putExtra("nickname", nickname)
            putExtra("datetime", dateTime)
        }
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }

    private fun messenger(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}