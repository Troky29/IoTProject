package com.example.iotproject.fragments.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iotproject.R
import java.util.ArrayList

class ActivityFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO: This should be removed when we implement the call for obtaining the activities"
        val item = ActivityCardItem(R.drawable.hqdefault, "Element", "29/12/2020")
        val exampleList = ArrayList<ActivityCardItem>()
        for (i in 1..100) exampleList += item

        val recyclerView: RecyclerView = view.findViewById(R.id.activityRecicleView)
        recyclerView.adapter = ActivityCardAdapter(exampleList)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        //TODO: See how you resolve the gate instance, and respond accordingly
        //val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }


}