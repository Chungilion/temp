package com.example.mobileapp.ui.account

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileapp.R
import com.example.mobileapp.api.AccountApi
import com.example.mobileapp.api.ApiClient
import com.example.mobileapp.model.CreateAccountRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountCreationActivity : AppCompatActivity() {

    private lateinit var accountApi: AccountApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        accountApi = ApiClient.retrofit.create(AccountApi::class.java)

        val createButton = findViewById<Button>(R.id.create_account_button)
        createButton.setOnClickListener {
            val username = findViewById<EditText>(R.id.username).text.toString()
            val password = findViewById<EditText>(R.id.password).text.toString()
            val email = findViewById<EditText>(R.id.email).text.toString()

            createAccount(CreateAccountRequest(username, password, email))
        }
    }

    private fun createAccount(request: CreateAccountRequest) {
        accountApi.createAccount(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AccountCreationActivity, "Account Created", Toast.LENGTH_SHORT).show()
                    finish() // Close activity and return to login screen
                } else {
                    Toast.makeText(this@AccountCreationActivity, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@AccountCreationActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
