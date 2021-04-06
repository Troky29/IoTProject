package com.example.iotproject.fragments.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.iotproject.MainActivityViewModel
import com.example.iotproject.R

class RegisterCarDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_register_car, container, false)

        val colorTextView = view.findViewById<AutoCompleteTextView>(R.id.colorAutoCompleteTextView)
        val colors: Array<out String> = resources.getStringArray(R.array.car_colors)
        ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, colors).also { adapter ->
            colorTextView.setAdapter(adapter)
        }

        val brandTextView = view.findViewById<AutoCompleteTextView>(R.id.bradAutoCompleteTextView)
        val brands: Array<out String> = resources.getStringArray(R.array.car_brands)
        ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, brands).also { adapter ->
            brandTextView.setAdapter(adapter)
        }

        view.findViewById<Button>(R.id.confirmButton).setOnClickListener() {
            val license = view.findViewById<EditText>(R.id.licensePlateEditText).text.toString()
            val color = colorTextView.text.toString()
            val brand = brandTextView.text.toString()

            //TODO: implement a more complete control on the car license plate
            if (license.length != 7) {
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

            val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
            viewModel.addCar(license, color, brand)
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.75).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun messenger(message: String) = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}