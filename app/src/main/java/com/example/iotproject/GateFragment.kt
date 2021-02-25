package com.example.iotproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

class GateFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        view.findViewById<ImageView>(R.id.gate_image).setOnClickListener(){
            val showDescription = it.contentDescription.toString()
            Toast.makeText(context, showDescription, Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.button).setOnClickListener() {
            Toast.makeText(context, "Button pressed", Toast.LENGTH_SHORT).show()
            //TODO: Move activity to login, for changing user
        }

         */

        val item = GateCardItem(R.drawable.hqdefault, "Name", "Location", "State", "Id")
        val exampleList = ArrayList<GateCardItem>()
        for (i in 1..3) exampleList += item

        val recyclerView: RecyclerView = view.findViewById(R.id.gateRecycleView)
        recyclerView.adapter = GateCardAdapter(exampleList)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
    }
}