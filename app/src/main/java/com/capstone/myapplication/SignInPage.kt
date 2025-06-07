package com.capstone.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.ApiHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInPage : AppCompatActivity() {
    private val apiHelper = ApiHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        // 입력 필드와 버튼 참조 (findViewById 한 번만!)
        val usernameField = findViewById<EditText>(R.id.text_name)
        val emailField = findViewById<EditText>(R.id.text_email)
        val passwordField = findViewById<EditText>(R.id.text_password)
        val confirmPasswordField = findViewById<EditText>(R.id.text_password_check)
        val phoneField = findViewById<EditText>(R.id.text_phone)
        val signInButton = findViewById<RelativeLayout>(R.id.sign_in_button)

        // 회원가입 버튼 클릭 이벤트
        signInButton.setOnClickListener {
            val username = usernameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()
            val phone = phoneField.text.toString()

            // 입력 검증
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // REST API로 회원가입
            registerUser(username, email, password, phone)
        }
    }

    private fun registerUser(username: String, email: String, password: String, phone: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val success = apiHelper.register(username, email, password, phone)
            withContext(Dispatchers.Main) {
                if (success) {
                    Toast.makeText(this@SignInPage, "Registration successful!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@SignInPage, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
