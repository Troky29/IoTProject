package com.example.iotproject.fragments.more

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.example.iotproject.MainActivityViewModel
import com.example.iotproject.R
import java.text.SimpleDateFormat
import java.util.*

class SpecialRuleActivity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_special_rule)
        val colorTextView = findViewById<AutoCompleteTextView>(R.id.colorAutoCompleteTextView)
        val colors: Array<out String> = resources.getStringArray(R.array.car_colors)
        ArrayAdapter(this, android.R.layout.simple_list_item_1, colors).also { adapter ->
            colorTextView.setAdapter(adapter)
        }

        val brandTextView = findViewById<AutoCompleteTextView>(R.id.bradAutoCompleteTextView)
        val brands: Array<out String> = resources.getStringArray(R.array.car_brands)
        ArrayAdapter(this, android.R.layout.simple_list_item_1, brands).also { adapter ->
            brandTextView.setAdapter(adapter)
        }


        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        findViewById<ImageButton>(R.id.calendarImageButton).setOnClickListener() {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                DatePickerDialog(this,
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
        findViewById<ImageButton>(R.id.timeImageButton).setOnClickListener() {
            TimePickerDialog(this,
                    timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true).show()
        }

        findViewById<Button>(R.id.confirmButton).setOnClickListener() {
            val nickname = findViewById<TextView>(R.id.nicknameEditText).text.toString()
            val license = findViewById<EditText>(R.id.licensePlateEditText).text.toString()
            val color = colorTextView.text.toString()
            val brand = brandTextView.text.toString()
            val date = findViewById<EditText>(R.id.dateEditText).text.toString()
            val time = findViewById<EditText>(R.id.timeEditText).text.toString()

            val corrected = license.replace("\\s".toRegex(), "").toUpperCase(Locale.ROOT)
            val format = "^[A-Z]{2}[0-9]{3}[A-Z]{2}$".toRegex()

            if (nickname.isEmpty()) {
                messenger("Please insert a nickname")
                return@setOnClickListener
            }
            if (corrected.length != 7 || !corrected.matches(format)) {
                messenger("Incorrect license plate")
                return@setOnClickListener
            }
            if (!colors.contains(color)) {
                messenger("Please choose one of the suggested color")
                return@setOnClickListener
            }
            if (!brands.contains(brand)) {
                messenger("Please choose one of the suggested brand")
                return@setOnClickListener
            }
            //TODO: check for actual date and time format
            if(date.isEmpty()) {
                messenger("Insert a date (day/month/year)")
                return@setOnClickListener
            }
            if (time.isEmpty()) {
                messenger("Insert time (hour:minute")
                return@setOnClickListener
            }

            //TODO: see if we need to have a reference of these, otherwise we do not need db integration
            val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
            //TODO: implement the actual call to the server
            //TODO: wait for the correct execution of the task befor returning
            finish()
        }
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ITALY)
        findViewById<EditText>(R.id.dateEditText).setText(sdf.format(calendar.time))
    }

    private fun updateTimeInView() {
        val myFormat = "HH:mm"
        val sdf = SimpleDateFormat(myFormat, Locale.ITALY)
        findViewById<EditText>(R.id.timeEditText).setText(sdf.format(calendar.time))
    }

    private fun messenger(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}