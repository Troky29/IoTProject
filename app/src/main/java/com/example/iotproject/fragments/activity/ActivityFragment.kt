package com.example.iotproject.fragments.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.iotproject.IoTApplication
import com.example.iotproject.R
import java.util.ArrayList
import com.example.iotproject.Constants.Companion.State
import com.example.iotproject.fragments.gate.Gate

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
        viewModel.activityList.observe(viewLifecycleOwner, { activityList ->
            Log.i("ActivityFragment", activityList.toString())
            flushActivityCards()
            for (activity in activityList)
                addActivityCard(activity)
        })
        //TODO: decide a strategy for reloading activities
        viewModel.loadActivities()

        //TODO: used for testing, delete
//        viewModel.deleteAll()
//        val testActivities = listOf(
//            Activity(0, getString(R.string.test_gate_code), "1990-11-12 12:32:00", "Pending", null),
//            Activity(0, getString(R.string.test_gate_code), "2021-12-11 10:02:00", "Pending", null),
//            Activity(0, getString(R.string.test_gate_code), "2005-21-15 07:52:00", "Pending", null)
//        )
//        viewModel.insertAll(testActivities)
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

    private fun loading() {
        //TODO: implement UI modification while waiting for activity update
    }

    private fun messenger(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}