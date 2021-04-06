package com.example.iotproject.fragments.gate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.iotproject.R

class GateFragment: Fragment() {
    private val gateList by lazy { ArrayList<GateCardItem>() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_gate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.gateRecycleView)
        recyclerView.adapter = GateCardAdapter(gateList)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

        val snapHelper: SnapHelper = SnapHelperOneByOne()

        snapHelper.attachToRecyclerView(recyclerView)
        //TODO: we could move here this code, but should wait and render only after the result of operation
/*
        val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.getGates().observe(viewLifecycleOwner, { gates ->
            flushGateCards()
            for (gate in gates)
                addGateCard(gate.name, gate.location, gate.state, gate.id)
            //replaceFragment(gateFragment)
        })

 */
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        TODO("Maybe we can insert here te update of the gates")
    }

    fun addGateCard(name: String, location: String, state: String, id: String) {
        val item = GateCardItem(R.drawable.hqdefault, name, location, state, id)
        gateList.add(item)
    }

    //TODO: if we move the loading of the gate inside here we can remove the following two functions
    fun flushGateCards() { gateList.clear() }

    fun hideEmpty() {
        requireView().findViewById<TextView>(R.id.emptyGateTextView).visibility = View.INVISIBLE
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