package com.vishnu.lifenest

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.vishnu.lifenest.util.LockState
import com.vishnu.lifenest.util.Prefs

class PinLockActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_lock)

        val editPin = findViewById<EditText>(R.id.edit_pin_input)
        val btnUnlock = findViewById<Button>(R.id.btn_unlock)
        val textError = findViewById<TextView>(R.id.text_pin_error)

        btnUnlock.setOnClickListener {
            val entered = editPin.text.toString()
            val saved = Prefs.pinCode(this)
            if (entered.isNotBlank() && entered == saved) {
                LockState.unlocked = true
                finish()
            } else {
                textError.visibility = TextView.VISIBLE
                editPin.text.clear()
                Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        // Block back button -- user must enter the correct PIN or leave the app entirely.
        moveTaskToBack(true)
    }
}
