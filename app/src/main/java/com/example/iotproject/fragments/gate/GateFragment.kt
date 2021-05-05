package com.example.iotproject.fragments.gate

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
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.iotproject.IoTApplication
import com.example.iotproject.R
import com.example.iotproject.database.Gate
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GateFragment: Fragment(), GateCardAdapter.OnOpenListener {
    private val gateCardList by lazy { ArrayList<GateCardItem>() }
    private lateinit var recyclerView: RecyclerView
    private val viewModel: GateFragmentViewModel by viewModels {
        GateViewModelFactory((requireActivity().application as IoTApplication).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.gateRecycleView)
        recyclerView.adapter = GateCardAdapter(gateCardList, requireContext(), this)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

        val snapHelper: SnapHelper = SnapHelperOneByOne()
        snapHelper.attachToRecyclerView(recyclerView)

        viewModel.message.observe(viewLifecycleOwner, { message ->
            messenger(message)
        })
        viewModel.loading.observe(viewLifecycleOwner, { loading ->
            if (loading)
                view.findViewById<ProgressBar>(R.id.gateProgressBar)!!.visibility = View.VISIBLE
            else
                view.findViewById<ProgressBar>(R.id.gateProgressBar)!!.visibility = View.INVISIBLE
        })
        viewModel.gateList.observe(viewLifecycleOwner, { gateList ->
            val emptyGateTextView = view.findViewById<TextView>(R.id.emptyGateTextView)
            if (gateList.isEmpty()) {
                viewModel.loadGates()
                emptyGateTextView.visibility = View.VISIBLE
            } else {
                emptyGateTextView.visibility = View.INVISIBLE
                flushGateCards()
                for (gate in gateList)
                    addGateCard(gate)
            }
        })

        view.findViewById<FloatingActionButton>(R.id.addGateFAB).setOnClickListener() {
            val intent = Intent(context, RegisterGateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addGateCard(gate: Gate) {
        val item = GateCardItem(gate.name, gate.location, gate.imageURL)
        gateCardList.add(item)
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun flushGateCards() {
        gateCardList.clear()
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    override fun onClick(position: Int) {
        viewModel.openGate(position)
    }

    private fun messenger(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private class SnapHelperOneByOne : LinearSnapHelper() {
        override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager?, velocityX: Int, velocityY: Int): Int {
            if (layoutManager !is RecyclerView.SmoothScroller.ScrollVectorProvider)
                return RecyclerView.NO_POSITION

            val currentView: View = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION

            return layoutManager.getPosition(currentView)
        }
    }
}