package com.vishnu.lifenest

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vishnu.lifenest.ui.home.HomeFragment
import com.vishnu.lifenest.ui.settings.SettingsFragment
import com.vishnu.lifenest.util.Prefs

class MainActivity : BaseActivity() {

    private var pinCheckLauncherUsed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.main_bottom_nav)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_settings -> SettingsFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        if (Prefs.pinEnabled(this) && !Prefs.pinCode(this).isNullOrBlank() && !pinCheckLauncherUsed) {
            pinCheckLauncherUsed = true
            startActivity(Intent(this, PinLockActivity::class.java))
        }
    }

    override fun onPause() {
        super.onPause()
        pinCheckLauncherUsed = false
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .commit()
    }
}
