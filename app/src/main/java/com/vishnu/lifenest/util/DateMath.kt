package com.vishnu.lifenest.util

import java.text.SimpleDateFormat
import java.util.*

/** Calendar-aware year/month/day difference and display helpers for Day Counter. */
object DateMath {

    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun parseIso(dateStr: String): Calendar {
        val cal = Calendar.getInstance()
        cal.time = isoFormat.parse(dateStr) ?: Date()
        clearTime(cal)
        return cal
    }

    private fun clearTime(cal: Calendar) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }

    /** Returns (years, months, days) between two calendars, start assumed <= end. */
    fun yearsMonthsDays(start: Calendar, end: Calendar): Triple<Int, Int, Int> {
        var years = end.get(Calendar.YEAR) - start.get(Calendar.YEAR)
        var months = end.get(Calendar.MONTH) - start.get(Calendar.MONTH)
        var days = end.get(Calendar.DAY_OF_MONTH) - start.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months -= 1
            val prevMonthCal = end.clone() as Calendar
            prevMonthCal.add(Calendar.MONTH, -1)
            days += prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (months < 0) {
            years -= 1
            months += 12
        }
        return Triple(years, months, days)
    }

    fun daysBetween(start: Calendar, end: Calendar): Int {
        val diff = end.timeInMillis - start.timeInMillis
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    /** e.g. "168 days" or "5 months 10 days" or "1 year 2 months 5 days" */
    fun humanBreakdown(start: Calendar, end: Calendar): String {
        val (y, m, d) = yearsMonthsDays(start, end)
        return when {
            y > 0 -> "$y year${if (y != 1) "s" else ""} $m month${if (m != 1) "s" else ""} $d day${if (d != 1) "s" else ""}"
            m > 0 -> "$m month${if (m != 1) "s" else ""} $d day${if (d != 1) "s" else ""}"
            else -> "$d day${if (d != 1) "s" else ""}"
        }
    }

    /** For yearly recurring events: the next occurrence of month/day from today onward. */
    fun nextYearlyOccurrence(fromDate: Calendar): Calendar {
        val today = Calendar.getInstance()
        clearTime(today)
        val next = today.clone() as Calendar
        next.set(Calendar.MONTH, fromDate.get(Calendar.MONTH))
        next.set(Calendar.DAY_OF_MONTH, fromDate.get(Calendar.DAY_OF_MONTH))
        if (next.before(today) || next == today) {
            if (next.before(today)) next.add(Calendar.YEAR, 1)
        }
        return next
    }

    fun today(): Calendar {
        val cal = Calendar.getInstance()
        clearTime(cal)
        return cal
    }
}
