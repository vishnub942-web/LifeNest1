package com.vishnu.lifenest

import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView

class CrashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val log = intent.getStringExtra("crash_log") ?: "Unknown error"

        val textView = TextView(this).apply {
            text = "LifeNest crashed. Long-press below to select and copy this text, then send it:\n\n$log"
            setTextIsSelectable(true)
            setPadding(32, 32, 32, 32)
            setTextColor(android.graphics.Color.WHITE)
        }
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(android.graphics.Color.BLACK)
            addView(textView)
        }
        setContentView(scrollView)
    }
}
