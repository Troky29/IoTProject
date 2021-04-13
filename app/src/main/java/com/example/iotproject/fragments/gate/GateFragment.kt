package com.example.iotproject.fragments.gate

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
import com.example.iotproject.LoadingDialog
import com.example.iotproject.R

class GateFragment: Fragment() {
    private val gateCardList by lazy { ArrayList<GateCardItem>() }
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.gateRecycleView)
        recyclerView.adapter = GateCardAdapter(gateCardList, requireContext())
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

        val snapHelper: SnapHelper = SnapHelperOneByOne()
        snapHelper.attachToRecyclerView(recyclerView)

        val viewModel: GateFragmentViewModel by viewModels {
            GateViewModelFactory((requireActivity().application as IoTApplication).repository)
        }
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
            flushGateCards()
            for (gate in gateList)
                addGateCard(gate)
            if (gateList.isEmpty())
                emptyGateTextView.visibility = View.VISIBLE
        })

        //TODO: study a strategy for reloading gates
        viewModel.loadGates()

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