package com.example.iotproject.fragments.car

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iotproject.IoTApplication
import com.example.iotproject.R
import com.example.iotproject.database.Car
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CarFragment : Fragment() {
    private val carCardList by lazy { ArrayList<CarCardItem>() }
    private lateinit var recyclerView: RecyclerView
    private val viewModel: CarFragmentViewModel by viewModels {
        CarViewModelFactory((requireActivity().application as IoTApplication).repository)
    }
    private val REQUEST_CODE = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_car, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.carRecycleView)
        recyclerView.adapter = CarCardAdapter(carCardList)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        viewModel.message.observe(viewLifecycleOwner, { message -> messenger(message) })
        viewModel.loading.observe(viewLifecycleOwner, { loading ->
            if (loading)
                view.findViewById<ProgressBar>(R.id.carProgressBar)!!.visibility = View.VISIBLE
            else
                view.findViewById<ProgressBar>(R.id.carProgressBar)!!.visibility = View.INVISIBLE
        })
        viewModel.carList.observe(viewLifecycleOwner, { carList ->
            val emptyCarTextView = view.findViewById<TextView>(R.id.emptyCarTextView)
            if(carList.isEmpty()) {
                viewModel.loadCars()
                emptyCarTextView.visibility = View.VISIBLE
            } else {
                flushCarCards()
                for (car in carList)
                    addCarCard(car)
            }
        })

        view.findViewById<FloatingActionButton>(R.id.addCarFAB).setOnClickListener {
            val registerCarDialog = RegisterCarDialog()
            registerCarDialog.setTargetFragment(this, REQUEST_CODE)
            registerCarDialog.show(parentFragmentManager, "RegisterCardDialog")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data != null){
            val license = data.getStringExtra("license") ?: return
            val color = data.getStringExtra("color") ?: return
            val brand = data.getStringExtra("brand") ?:return
            val isGuest = data.getBooleanExtra("isGuest", false)
            if (isGuest){
                val nickname = data.getStringExtra("nickname") ?: return
                val datetime = data.getStringExtra("datetime") ?: return
                viewModel.addSpecialRule(nickname, license, color, brand, datetime)
            } else {
                viewModel.addCar(license, color, brand)
            }
        }
    }

    private fun flushCarCards() {
        carCardList.clear()
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun addCarCard(car: Car) {
        val item = CarCardItem(car.license, car.color, car.brand, car.nickname, car.deadline)
        carCardList.add(item)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun messenger(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}