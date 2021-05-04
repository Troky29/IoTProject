package com.example.iotproject.fragments.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iotproject.IoTApplication
import com.example.iotproject.R
import java.util.ArrayList
import com.example.iotproject.Constants.Companion.State
import com.example.iotproject.database.Activity

class ActivityFragment: Fragment(),ActivityCardAdapter.OnActionListener {
    private val activityCardList by lazy { ArrayList<ActivityCardItem>() }
    private lateinit var recyclerView: RecyclerView
    private val viewModel: ActivityFragmentViewModel by viewModels {
        ActivityViewModelFactory((requireActivity().application as IoTApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.activityRecycleView)
        recyclerView.adapter = ActivityCardAdapter(activityCardList, requireContext(), this)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)


        viewModel.message.observe(viewLifecycleOwner, { message ->
            messenger(message)
        })
        viewModel.loading.observe(viewLifecycleOwner, { loading ->
            if (loading)
                view.findViewById<ProgressBar>(R.id.activityProgressBar)!!.visibility = View.VISIBLE
            else
                view.findViewById<ProgressBar>(R.id.activityProgressBar)!!.visibility = View.INVISIBLE
        })
        viewModel.activityList.observe(viewLifecycleOwner, { activityList ->
            val emptyActivityTextView = view.findViewById<TextView>(R.id.emptyActivityTextView)
            if (activityList.isEmpty())
                emptyActivityTextView.visibility = View.VISIBLE
            flushActivityCards()
            for (activity in activityList)
                addActivityCard(activity)
        })

        viewModel.loadActivities()
    }

    private fun addActivityCard(activity: Activity) {
        val gateName = viewModel.getGateName(activity.gate) ?: "Missing name"
        val item = ActivityCardItem(gateName, activity.state,activity.datetime, activity.imageURL)
        activityCardList.add(item)
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun flushActivityCards() {
        activityCardList.clear()
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    override fun onClick(position: Int, action: State) {
        when (action) {
            State.ALLOW -> viewModel.setAction(position, "Granted")
            State.DENY -> viewModel.setAction(position, "Denied")
            State.REPORT -> viewModel.setAction(position, "Reported")
            else -> Log.e("ActivityFragment", "Received unexpected action")
        }
    }

    private fun messenger(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}