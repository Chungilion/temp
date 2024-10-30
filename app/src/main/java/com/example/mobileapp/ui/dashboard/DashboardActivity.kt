package com.example.mobileapp.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileapp.R
import com.example.mobileapp.api.AccountApi
import com.example.mobileapp.api.ApiClient
import com.example.mobileapp.model.UserInfo
import com.example.mobileapp.ui.account.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var accountApi: AccountApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize Retrofit API client
        accountApi = ApiClient.retrofit.create(AccountApi::class.java)

        // Get references to UI components
        val userImage = findViewById<ImageView>(R.id.user_image)
        val userNameTextView = findViewById<TextView>(R.id.user_name)
        val jobTitleTextView = findViewById<TextView>(R.id.job_title)
        val updateInfoButton = findViewById<Button>(R.id.btn_update_info)
        val attendanceButton = findViewById<Button>(R.id.btn_attendance)
        val payrollButton = findViewById<Button>(R.id.btn_payroll)
        val supportButton = findViewById<Button>(R.id.btn_support)
        val logoutButton = findViewById<Button>(R.id.logout_button)

        // Set button click listeners
        updateInfoButton.setOnClickListener {
            // Implement the action for updating user info
            Toast.makeText(this, "Update Info clicked", Toast.LENGTH_SHORT).show()
        }

        attendanceButton.setOnClickListener {
            // Implement the action for attendance
            Toast.makeText(this, "Attendance clicked", Toast.LENGTH_SHORT).show()
        }

        payrollButton.setOnClickListener {
            // Implement the action for payroll
            Toast.makeText(this, "Payroll clicked", Toast.LENGTH_SHORT).show()
        }

        supportButton.setOnClickListener {
            // Implement the action for support
            Toast.makeText(this, "Support clicked", Toast.LENGTH_SHORT).show()
        }

        logoutButton.setOnClickListener {
            performLogout()
        }

        // Fetch and display user data on startup
        fetchUserData(userNameTextView, jobTitleTextView)
    }

    // Function to fetch user data from the API and display it
    private fun fetchUserData(userNameTextView: TextView, jobTitleTextView: TextView) {
        accountApi.getUserInfo().enqueue(object : Callback<UserInfo> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {
                if (response.isSuccessful) {
                    // Update the TextViews with the fetched data
                    val userInfo = response.body()
                    userNameTextView.text = userInfo?.name ?: "Unknown Name"
                    jobTitleTextView.text = userInfo?.jobTitle ?: "Unknown Title"
                } else {
                    // Handle API errors
                    Toast.makeText(this@DashboardActivity, "Failed to fetch user info", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserInfo>, t: Throwable) {
                // Handle failure
                Toast.makeText(this@DashboardActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function to handle logout (navigate back to login screen)
    private fun performLogout() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close the dashboard activity
    }
}
