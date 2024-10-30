package com.example.mobileapp.ui.account

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileapp.R
import com.example.mobileapp.api.ApiService
import com.example.mobileapp.model.LoginRequest
import com.example.mobileapp.model.LoginResponse
import com.example.mobileapp.ui.dashboard.AdminDashboardActivity
import com.example.mobileapp.ui.dashboard.DashboardActivity
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    companion object {
        // Hardcoded account details
        private const val EMPLOYEE_USERNAME = "employee"
        private const val EMPLOYEE_PASSWORD = "employee123"

        private const val ADMIN_USERNAME = "admin"
        private const val ADMIN_PASSWORD = "admin123"
    }

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:9090")  // Use localhost equivalent for emulator
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        val loginButton = findViewById<Button>(R.id.login_button)
        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            performLogin(username, password)
        }
    }

    private fun performLogin(username: String, password: String) {
        val loginRequest = LoginRequest(username, password)

        // Attempt database login via API service
        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    handleRoleBasedNavigation(username, password)
                } else {
                    handleHardcodedCredentials(username, password)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                handleHardcodedCredentials(username, password)
            }
        })
    }

    private fun handleRoleBasedNavigation(username: String, password: String) {
        if (username == ADMIN_USERNAME && password == ADMIN_PASSWORD) {
            Toast.makeText(this, "Login successful as Admin", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
        } else if (username == EMPLOYEE_USERNAME && password == EMPLOYEE_PASSWORD) {
            Toast.makeText(this, "Login successful as Employee", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleHardcodedCredentials(username: String, password: String) {
        if (username == ADMIN_USERNAME && password == ADMIN_PASSWORD) {
            Toast.makeText(this, "Login successful as Admin (fallback)", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
        } else if (username == EMPLOYEE_USERNAME && password == EMPLOYEE_PASSWORD) {
            Toast.makeText(this, "Login successful as Employee (fallback)", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }
    }
}
