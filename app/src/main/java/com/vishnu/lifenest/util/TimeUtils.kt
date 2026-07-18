package com.vishnu.lifenest.util

object TimeUtils {

    /**
     * Converts raw digit input like "0530" into "05:30".
     * If the text already contains a colon, returns it unchanged.
     * Handles 1-4 digit input gracefully (e.g. "530" -> "05:30").
     */
    fun formatDigitsToTime(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        if (digits.isEmpty()) return ""
        val padded = digits.padStart(4, '0').takeLast(4)
        val hh = padded.substring(0, 2)
        val mm = padded.substring(2, 4)
        return "$hh:$mm"
    }

    /**
     * Combines a "HH:mm" time with AM/PM into a display string, e.g. "05:30 AM"
     */
    fun withMeridiem(hhmm: String, isAm: Boolean): String {
        return "$hhmm ${if (isAm) "AM" else "PM"}"
    }

    /**
     * Given start "05:00 AM" and end "05:30 AM" style strings, returns
     * spent minutes. Returns null if either time is missing/invalid.
     */
    fun spentMinutes(start: String?, end: String?): Int? {
        if (start.isNullOrBlank() || end.isNullOrBlank()) return null
        val startMin = to24hMinutes(start) ?: return null
        val endMin = to24hMinutes(end) ?: return null
        var diff = endMin - startMin
        if (diff < 0) diff += 24 * 60 // crosses midnight
        return diff
    }

    fun minutesToLabel(minutes: Int?): String {
        if (minutes == null) return ""
        val h = minutes / 60
        val m = minutes % 60
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }

    private fun to24hMinutes(display: String): Int? {
        // expects "HH:mm AM" or "HH:mm PM"
        val parts = display.trim().split(" ")
        if (parts.size != 2) return null
        val (time, meridiem) = parts
        val timeParts = time.split(":")
        if (timeParts.size != 2) return null
        var hour = timeParts[0].toIntOrNull() ?: return null
        val minute = timeParts[1].toIntOrNull() ?: return null
        if (meridiem.equals("AM", ignoreCase = true)) {
            if (hour == 12) hour = 0
        } else {
            if (hour != 12) hour += 12
        }
        return hour * 60 + minute
    }
}
