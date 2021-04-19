package com.example.iotproject.fragments.car

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.iotproject.IoTApplication
import com.example.iotproject.R
import com.example.iotproject.fragments.gate.GateFragmentViewModel
import com.example.iotproject.fragments.gate.GateViewModelFactory
import com.example.iotproject.login.Login
import com.example.iotproject.login.LoginViewModel

class CarFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        view.findViewById<Button>(R.id.registerCarButton).setOnClickListener {
            RegisterCarDialog().show(childFragmentManager, "RegisterCarDialog")
        }

        view.findViewById<Button>(R.id.addSpecialRuleButton).setOnClickListener {
            val intent = Intent(context, SpecialRuleActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.loguotButton).setOnClickListener { logout() }

        view.findViewById<Button>(R.id.deleteGateButton).setOnClickListener { deleteAllGates() }
    }

    private fun deleteAllGates() {
        val viewModel: GateFragmentViewModel by viewModels {
            GateViewModelFactory((requireActivity().application as IoTApplication).repository)
        }
        viewModel.deleteAll()
        //TODO: eventually make also a call to delete the servers gate
    }

    private fun logout() {
        val intent = Intent(context, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.logout()
        startActivity(intent)
    }
}