package com.capstone.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        // 입력 필드와 버튼 참조
        val usernameField = findViewById<EditText>(R.id.text_name).findViewById<EditText>(R.id.text_name)
        val emailField = findViewById<EditText>(R.id.text_email).findViewById<EditText>(R.id.text_email)
        val passwordField = findViewById<EditText>(R.id.text_password).findViewById<EditText>(R.id.text_password)
        val confirmPasswordField = findViewById<EditText>(R.id.text_password_check).findViewById<EditText>(R.id.text_password_check)
        val phoneField = findViewById<EditText>(R.id.text_phone).findViewById<EditText>(R.id.text_phone)
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

            // 회원가입 로직
            registerUser(username, email, password, phone)
        }
    }

    private fun registerUser(username: String, email: String, password: String, phone: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val dbHelper = DatabaseHelper(applicationContext)
            val connection = dbHelper.connect()

            if (connection != null) {
                val hashedPassword = hashPassword(password) // 비밀번호 해싱
                val query = """
                    INSERT INTO user_info.users (username, password_hash, email, phone_number) 
                    VALUES (?, ?, ?, ?)
                """
                try {
                    val statement = connection.prepareStatement(query)
                    statement.setString(1, username)
                    statement.setString(2, hashedPassword)
                    statement.setString(3, email)
                    statement.setString(4, phone)
                    statement.executeUpdate()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignInPage, "Registration successful!", Toast.LENGTH_SHORT).show()
                        finish() // 회원가입 후 액티비티 종료
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignInPage, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    connection.close()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignInPage, "Database connection failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // SHA-256 비밀번호 해싱 함수
    private fun hashPassword(password: String): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}