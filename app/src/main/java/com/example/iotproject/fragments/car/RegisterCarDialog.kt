package com.example.iotproject.fragments.car

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment
import com.example.iotproject.R
import com.example.iotproject.database.Car
import com.example.iotproject.fragments.activity.ActivityCardAdapter
import java.util.*

class RegisterCarDialog : DialogFragment() {
    private lateinit var result: Intent
    private val REQUEST_CODE = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register_car, container, false)


        val colorTextView = view.findViewById<AutoCompleteTextView>(R.id.colorAutoCompleteTextView)
        val colors: Array<out String> = resources.getStringArray(R.array.car_colors)
        ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, colors).also { adapter ->
            colorTextView.setAdapter(adapter)
        }

        val brandTextView = view.findViewById<AutoCompleteTextView>(R.id.bradAutoCompleteTextView)
        val brands: Array<out String> = resources.getStringArray(R.array.car_brands)
        ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, brands).also { adapter ->
            brandTextView.setAdapter(adapter)
        }

        view.findViewById<Button>(R.id.confirmButton).setOnClickListener() {
            val license = view.findViewById<EditText>(R.id.licensePlateEditText).text.toString()
            val color = colorTextView.text.toString().trim()
            val brand = brandTextView.text.toString().trim()
            val isGuest = !view.findViewById<SwitchCompat>(R.id.ownerSwitchCompact).isChecked

            val correctedLicense = license.replace("\\s".toRegex(), "").toUpperCase(Locale.ROOT)
            val format = "^[A-Z]{2}[0-9]{3}[A-Z]{2}$".toRegex()
            if (correctedLicense.length != 7 || !correctedLicense.matches(format)) {
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

            result = getResult(license, color, brand, isGuest)
            if (isGuest) {
                //TODO: create the new routine
                val specialRuleDialog = SpecialRuleDialog()
                specialRuleDialog.setTargetFragment(this, REQUEST_CODE)
                specialRuleDialog.show(parentFragmentManager, "SpecialRuleDialog")
            } else {
                sendResult(result)
            }
            //TODO: see if we need to have a reference of these, otherwise we do not need db integration
            //sendResult(license, color, brand, isGuest)
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data != null) {
            result.apply {
                putExtra("nickname", data.getStringExtra("nickname"))
                putExtra("datetime", data.getStringExtra("datetime"))
            }
            sendResult(result)
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.75).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun getResult(license: String, color: String, brand: String, isGuest: Boolean) =
        Intent().apply {
            putExtra("license", license)
            putExtra("color", color)
            putExtra("brand", brand)
            putExtra("isGuest", isGuest)
        }

    private fun sendResult(intent: Intent) {
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }

    private fun messenger(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}