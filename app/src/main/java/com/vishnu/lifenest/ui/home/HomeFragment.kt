package com.vishnu.lifenest.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.vishnu.lifenest.R
import com.vishnu.lifenest.ui.calendar.TaskCalendarFragment
import com.vishnu.lifenest.ui.creditdebt.CreditDebtFragment
import com.vishnu.lifenest.ui.dailytask.DailyTaskFragment
import com.vishnu.lifenest.ui.daycounter.DayCounterFragment
import com.vishnu.lifenest.ui.notepad.NotepadFragment
import com.vishnu.lifenest.ui.todo.ToDoFragment
import com.vishnu.lifenest.util.Prefs

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            loadChild(DailyTaskFragment())
        }

        view.findViewById<View>(R.id.nav_daily_task).setOnClickListener { loadChild(DailyTaskFragment()) }
        view.findViewById<View>(R.id.nav_calendar).setOnClickListener { loadChild(TaskCalendarFragment()) }
        view.findViewById<View>(R.id.nav_todo).setOnClickListener { loadChild(ToDoFragment()) }
        view.findViewById<View>(R.id.nav_notepad).setOnClickListener { loadChild(NotepadFragment()) }
        view.findViewById<View>(R.id.nav_daycounter).setOnClickListener { loadChild(DayCounterFragment()) }
        view.findViewById<View>(R.id.nav_creditdebt).setOnClickListener { loadChild(CreditDebtFragment()) }

        // Scale the bottom-nav icons along with the font-size setting.
        val scale = Prefs.fontScale(requireContext())
        val baseSizePx = (24 * resources.displayMetrics.density * scale).toInt()
        val iconIds = listOf(
            R.id.icon_daily_task, R.id.icon_calendar, R.id.icon_todo,
            R.id.icon_notepad, R.id.icon_daycounter, R.id.icon_creditdebt
        )
        for (id in iconIds) {
            val iv = view.findViewById<ImageView>(id)
            val params = iv.layoutParams
            params.width = baseSizePx
            params.height = baseSizePx
            iv.layoutParams = params
        }
    }

    private fun loadChild(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.home_fragment_container, fragment)
            .commit()
    }
}
