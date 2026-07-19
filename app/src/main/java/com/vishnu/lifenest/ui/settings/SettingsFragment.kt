package com.vishnu.lifenest.ui.settings

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.vishnu.lifenest.R
import com.vishnu.lifenest.util.Prefs

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = Prefs.get(requireContext())

        val switchNotifications = view.findViewById<Switch>(R.id.switch_notifications)
        val switchDarkMode = view.findViewById<Switch>(R.id.switch_dark_mode)
        val switchSetPin = view.findViewById<Switch>(R.id.switch_set_pin)
        val btnSmall = view.findViewById<Button>(R.id.btn_font_small)
        val btnMedium = view.findViewById<Button>(R.id.btn_font_medium)
        val btnLarge = view.findViewById<Button>(R.id.btn_font_large)

        switchNotifications.isChecked = Prefs.notificationsEnabled(requireContext())
        switchDarkMode.isChecked = Prefs.darkModeEnabled(requireContext())
        switchSetPin.isChecked = Prefs.pinEnabled(requireContext())

        switchNotifications.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("notifications_enabled", checked).apply()
            if (checked) requestNotificationPermissionIfNeeded()
        }

        switchDarkMode.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean("dark_mode_enabled", checked).apply()
            Toast.makeText(
                requireContext(),
                "Noted. LifeNest is dark-themed throughout for now -- a full Light Mode is a bigger visual update we can build next.",
                Toast.LENGTH_LONG
            ).show()
        }

        switchSetPin.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                showSetPinDialog(switchSetPin)
            } else {
                prefs.edit().putBoolean("pin_enabled", false).apply()
                Prefs.clearPinCode(requireContext())
            }
        }

        btnSmall.setOnClickListener { applyFontScale(0.85f) }
        btnMedium.setOnClickListener { applyFontScale(1.0f) }
        btnLarge.setOnClickListener { applyFontScale(1.15f) }
    }

    private fun applyFontScale(scale: Float) {
        Prefs.setFontScale(requireContext(), scale)
        Toast.makeText(requireContext(), "Font size updated. Reopening LifeNest to apply it...", Toast.LENGTH_SHORT).show()
        // Restart the app so the new font scale takes effect everywhere (icons resized too).
        val intent = requireActivity().packageManager.getLaunchIntentForPackage(requireActivity().packageName)
        if (intent != null) {
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun showSetPinDialog(switchSetPin: Switch) {
        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        input.hint = "Enter a 4-digit PIN"

        AlertDialog.Builder(requireContext())
            .setTitle("Set PIN")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val pin = input.text.toString()
                if (pin.length in 4..8) {
                    Prefs.setPinCode(requireContext(), pin)
                    Prefs.get(requireContext()).edit().putBoolean("pin_enabled", true).apply()
                    Toast.makeText(requireContext(), "PIN set. LifeNest will now ask for it on open.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "PIN must be 4-8 digits", Toast.LENGTH_SHORT).show()
                    switchSetPin.isChecked = false
                }
            }
            .setNegativeButton("Cancel") { _, _ -> switchSetPin.isChecked = false }
            .setCancelable(false)
            .show()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}
