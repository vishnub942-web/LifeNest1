package com.vishnu.lifenest.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import com.vishnu.lifenest.R

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = requireContext().getSharedPreferences("lifenest_settings", Context.MODE_PRIVATE)

        val switchNotifications = view.findViewById<Switch>(R.id.switch_notifications)
        val switchDarkMode = view.findViewById<Switch>(R.id.switch_dark_mode)
        val switchSetPin = view.findViewById<Switch>(R.id.switch_set_pin)

        switchNotifications.isChecked = prefs.getBoolean("notifications_enabled", true)
        switchDarkMode.isChecked = prefs.getBoolean("dark_mode_enabled", true)
        switchSetPin.isChecked = prefs.getBoolean("pin_enabled", false)

        switchNotifications.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("notifications_enabled", checked).apply()
        }
        switchDarkMode.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("dark_mode_enabled", checked).apply()
            // TODO (Phase 2): apply AppCompatDelegate.setDefaultNightMode() here
        }
        switchSetPin.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("pin_enabled", checked).apply()
            // TODO (Phase 2): when turned on, show a dialog to set the 4-digit PIN,
            // and check it on app launch before showing MainActivity content.
        }
    }
}
