package com.vishnu.lifenest

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.vishnu.lifenest.util.Prefs

/** All activities extend this so the font-size setting applies everywhere. */
open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val scale = Prefs.fontScale(newBase)
        val config = Configuration(newBase.resources.configuration)
        config.fontScale = scale
        val newContext = newBase.createConfigurationContext(config)
        super.attachBaseContext(newContext)
    }
}
