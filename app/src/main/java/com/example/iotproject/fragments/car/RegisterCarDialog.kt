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
            if (isGuest) {
                //TODO: create the new routine
                SpecialRuleDialog().show(parentFragmentManager, "SpecialRuleDialog")
            }
            //TODO: see if we need to have a reference of these, otherwise we do not need db integration
            sendResult(license, color, brand, isGuest)
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.75).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun sendResult(license: String, color: String, brand: String, isGuest: Boolean) {
        val intent = Intent().apply {
            putExtra("license", license)
            putExtra("color", color)
            putExtra("brand", brand)
            putExtra("isGuest", isGuest)
        }
        Log.i("DialogFragment", "$targetFragment")
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }

    private fun messenger(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}