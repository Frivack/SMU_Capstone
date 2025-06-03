package com.capstone.myapplication
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.ApiHelper        // 여기서 import
import com.example.capstone.LoginResponse    // 여기서 import
import com.example.capstone.UserData         // 여기서 import
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginPage : AppCompatActivity() {
    // ✅ 중복 선언하지 말 것! 아래 한 번만
    private val apiHelper = ApiHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)

        setupLoginButton()
        setupSwitchButtons()
    }

    private fun setupLoginButton() {
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
            try {
                // username을 email로 임시 세팅
                val loginResponse = apiHelper.login(email, password, email)
                withContext(Dispatchers.Main) {
                    android.util.Log.e("LoginResponse", loginResponse?.toString() ?: "null")
                    if (loginResponse == null) {
                        Toast.makeText(this@LoginPage, "서버 오류, 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                        return@withContext
                    }
                    if (loginResponse?.message == "Login successful" && loginResponse.user != null) {
                        Toast.makeText(this@LoginPage, "Login successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginPage, MainPage::class.java)
                        intent.putExtra("USER_ID", loginResponse.user.user_id)
                        intent.putExtra("USER_EMAIL", loginResponse.user.email)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginPage, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginPage, "예외 발생: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    private fun setupSwitchButtons() {
        val newId = findViewById<TextView>(R.id.new_id)

        newId.setOnClickListener {
            val intent = Intent(this, SignInPage::class.java)
            startActivity(intent)
        }
    }
}
