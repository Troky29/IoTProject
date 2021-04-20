package com.example.iotproject.fragments.car

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iotproject.IoTApplication
import com.example.iotproject.R
import com.example.iotproject.database.Car
import com.example.iotproject.fragments.activity.ActivityCardAdapter
import com.example.iotproject.fragments.gate.GateFragmentViewModel
import com.example.iotproject.fragments.gate.GateViewModelFactory
import com.example.iotproject.login.Login
import com.example.iotproject.login.LoginViewModel
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

        //val viewModel = ViewModelProvider(this).get(CarFragmentViewModel::class.java)

        viewModel.message.observe(viewLifecycleOwner, { message -> messenger(message) })
        viewModel.carList.observe(viewLifecycleOwner, { carList ->
            flushCarCards()
            for (car in carList)
                addCarCard(car)
        })

        view.findViewById<FloatingActionButton>(R.id.addCarFAB).setOnClickListener {
            val registerCarDialog = RegisterCarDialog()
            registerCarDialog.setTargetFragment(this, REQUEST_CODE)
            registerCarDialog.show(parentFragmentManager, "RegisterCardDialog")
            //SpeAlertDialog.Builder()
        }

    //TODO: change accordingly
        /*
        view.findViewById<Button>(R.id.registerCarButton).setOnClickListener {
            RegisterCarDialog().show(childFragmentManager, "RegisterCarDialog")
        }

        view.findViewById<Button>(R.id.addSpecialRuleButton).setOnClickListener {
            val intent = Intent(context, SpecialRuleActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.loguotButton).setOnClickListener { logout() }

        view.findViewById<Button>(R.id.deleteGateButton).setOnClickListener { deleteAllGates() }

         */
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

    //TODO: eventually remove this, not needed
    private fun deleteAllGates() {
        val viewModel: GateFragmentViewModel by viewModels {
            GateViewModelFactory((requireActivity().application as IoTApplication).repository)
        }
        viewModel.deleteAll()
        //TODO: eventually make also a call to delete the servers gate
    }

    //TODO: move to the user fragment
    private fun logout() {
        val intent = Intent(context, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.logout()
        startActivity(intent)
    }

    private fun flushCarCards() {
        carCardList.clear()
        recyclerView.adapter?.notifyDataSetChanged()
    }

    //TODO: update with the correct card information, and distinguish between the two
    private fun addCarCard(car: Car) {
        if (car.isGuest) {
            //TODO: load the other card, maybe calculate how much time is left
            return
        } else {
            val item = CarCardItem(car.license, car.color, car.brand)
            carCardList.add(item)
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun messenger(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}