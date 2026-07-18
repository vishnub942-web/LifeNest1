package com.vishnu.lifenest.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vishnu.lifenest.R
import com.vishnu.lifenest.ui.calendar.TaskCalendarFragment
import com.vishnu.lifenest.ui.creditdebt.CreditDebtFragment
import com.vishnu.lifenest.ui.dailytask.DailyTaskFragment
import com.vishnu.lifenest.ui.daycounter.DayCounterFragment
import com.vishnu.lifenest.ui.notepad.NotepadFragment
import com.vishnu.lifenest.ui.todo.ToDoFragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNav = view.findViewById<BottomNavigationView>(R.id.home_bottom_nav)

        if (savedInstanceState == null) {
            loadChild(DailyTaskFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_daily_task -> DailyTaskFragment()
                R.id.nav_calendar -> TaskCalendarFragment()
                R.id.nav_todo -> ToDoFragment()
                R.id.nav_notepad -> NotepadFragment()
                R.id.nav_daycounter -> DayCounterFragment()
                R.id.nav_creditdebt -> CreditDebtFragment()
                else -> DailyTaskFragment()
            }
            loadChild(fragment)
            true
        }
    }

    private fun loadChild(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container, fragment)
            .commit()
    }
}
