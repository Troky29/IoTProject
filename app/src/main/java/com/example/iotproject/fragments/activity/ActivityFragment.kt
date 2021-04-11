package com.example.iotproject.fragments.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iotproject.IoTApplication
import com.example.iotproject.R
import java.util.ArrayList

class ActivityFragment: Fragment() {
    private val activityCardList by lazy { ArrayList<ActivityCardItem>() }
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.activityRecycleView)
        recyclerView.adapter = ActivityCardAdapter(activityCardList)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        val viewModel: ActivityFragmentViewModel by viewModels {
            ActivityViewModelFactory((requireActivity().application as IoTApplication).repository)
        }
        viewModel.message.observe(viewLifecycleOwner, { message ->
            messenger(message)
        })
        viewModel.activityList.observe(viewLifecycleOwner, { activityList ->
            flushActivityCards()
            for (activity in activityList)
                addActivityCard(activity)
        })
    }

    private fun addActivityCard(activity: Activity) {
        val item = ActivityCardItem(R.drawable.hqdefault, activity.state,activity.datetime)
        activityCardList.add(item)
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun flushActivityCards() {
        activityCardList.clear()
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun messenger(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}