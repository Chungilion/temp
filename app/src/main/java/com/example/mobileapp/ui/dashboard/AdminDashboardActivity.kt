package com.example.mobileapp.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileapp.R
import com.example.mobileapp.ui.account.LoginActivity
import com.example.mobileapp.ui.hr.HRManagementActivity  // Import the HR activity

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Set button click listeners for management buttons
        findViewById<Button>(R.id.btn_finance_management).setOnClickListener {
            Toast.makeText(this, "Finance Management clicked", Toast.LENGTH_SHORT).show()
        }

        // HR Management Button: Navigate to HRManagementActivity
        findViewById<Button>(R.id.btn_hr_management).setOnClickListener {
            val intent = Intent(this, HRManagementActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_inventory_management).setOnClickListener {
            Toast.makeText(this, "Inventory Management clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_payroll_management).setOnClickListener {
            Toast.makeText(this, "Payroll Management clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_account_allocation).setOnClickListener {
            Toast.makeText(this, "Account Allocation clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_asset_management).setOnClickListener {
            Toast.makeText(this, "Asset Management clicked", Toast.LENGTH_SHORT).show()
        }

        // Bottom buttons
        findViewById<Button>(R.id.btn_business_operations).setOnClickListener {
            Toast.makeText(this, "Business Operations clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_message).setOnClickListener {
            Toast.makeText(this, "Message clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            performLogout()
        }
    }

    private fun performLogout() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close the admin dashboard activity
    }
}
