package com.example.mobileapp.ui.hr

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileapp.R

class HRManagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hr_management)

        // Button click listeners
        findViewById<Button>(R.id.btn_add).setOnClickListener {
            Toast.makeText(this, "Add clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
            Toast.makeText(this, "Dismiss clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_change_information).setOnClickListener {
            Toast.makeText(this, "Change Information clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_other_action).setOnClickListener {
            Toast.makeText(this, "Other Action clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
