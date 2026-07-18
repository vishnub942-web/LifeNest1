package com.vishnu.lifenest.ui.daycounter

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * Placeholder for Phase 2. Full requirements already gathered in chat with Vishnu;
 * this will be built out next using the same Room DB + ViewModel pattern as
 * DailyTaskFragment / TaskCalendarFragment.
 */
class DayCounterFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val tv = TextView(requireContext())
        tv.text = "Day Counter module — coming in Phase 2"
        tv.gravity = Gravity.CENTER
        tv.setTextColor(Color.WHITE)
        tv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return tv
    }
}
