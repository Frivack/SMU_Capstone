package com.capstone.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)

        setupLoginButton()

        setupSwitchButtons()
    }

    private fun setupLoginButton()
    {
        val passwordField = findViewById<EditText>(R.id.login_password_text)
        val loginButton = findViewById<TextView>(R.id.summit)

        loginButton.setOnClickListener {
            val emailField = findViewById<EditText>(R.id.login_email_text)
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authenticateUser(email, password)
            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun authenticateUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val dbHelper = DatabaseHelper(applicationContext)
            val connection = dbHelper.connect()

            if (connection != null) {
                val query = "SELECT user_id, password_hash FROM user_info.users WHERE email = ?"
                try {
                    val statement = connection.prepareStatement(query)
                    statement.setString(1, email)
                    val resultSet = statement.executeQuery()

                    if (resultSet.next()) {
                        val userId = resultSet.getInt("user_id") // user_id 조회
                        val storedHash = resultSet.getString("password_hash")
                        val inputHash = hashPassword(password)

                        if (storedHash == inputHash) {
                            // 로그인 성공
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@LoginPage, "Login successful!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@LoginPage, MainPage::class.java)
                                intent.putExtra("USER_ID", userId) // 사용자 ID 추가
                                intent.putExtra("USER_EMAIL", email) // 이메일도 추가
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            // 비밀번호가 일치하지 않음
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@LoginPage, "Invalid email or password", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        // 이메일이 존재하지 않음
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@LoginPage, "Invalid email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginPage, "Database query failed", Toast.LENGTH_SHORT).show()
                    }
                } finally {
                    connection.close()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginPage, "Database connection failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun setupSwitchButtons()
    {
        val newId = findViewById<TextView>(R.id.new_id)

        newId.setOnClickListener {
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, SignInPage::class.java)
            startActivity(intent)
        }
    }
}