package com.capstone.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainpage)

        // Intent에서 이메일 가져오기
        val email = intent.getStringExtra("USER_EMAIL")
        val userId = intent.getIntExtra("USER_ID", 0)

        // 이메일이 null이 아닌 경우 TextView에 설정
        email?.let {
            val mainPageIdTextView = findViewById<TextView>(R.id.mainpage_id)
            mainPageIdTextView.text = extractEmailPrefix(it)
        }

        // ========== 챗봇 버튼 및 컨테이너 설정 ==========
        val fabChatbot: FloatingActionButton = findViewById(R.id.fab_chatbot)
        val chatbotContainer: FragmentContainerView = findViewById(R.id.chatbot_fragment_container)

        fabChatbot.setOnClickListener {
            // 버튼 클릭 시 챗봇 프래그먼트 컨테이너를 보여주거나 숨김
            if (chatbotContainer.visibility == View.VISIBLE) {
                chatbotContainer.visibility = View.GONE
            } else {
                chatbotContainer.visibility = View.VISIBLE
            }
        }

        setupSwichButtons(email, userId)
    }

    // 이메일 앞부분 추출 함수
    private fun extractEmailPrefix(email: String): String {
        return email.substringBefore("@") // '@' 이전 문자열 반환
    }

    private fun setupSwichButtons(email: String?, userId: Int)
    {
        val build = findViewById<LinearLayout>(R.id.layout_build)
        val account = findViewById<LinearLayout>(R.id.layout_account)
        val review = findViewById<LinearLayout>(R.id.layout_review)
        val setting = findViewById<LinearLayout>(R.id.layout_setting)
        val support = findViewById<LinearLayout>(R.id.layout_support)
        val notice = findViewById<LinearLayout>(R.id.layout_notice)

        val build_button = findViewById<LinearLayout>(R.id.layout_build_button)

        val lowbarHome = findViewById<ImageView>(R.id.imageHomeOne)
        val lowbarBuild = findViewById<ImageView>(R.id.imageWhatidoOne)
        val lowbarSetting = findViewById<ImageView>(R.id.imageAutomaticOne)

        build.setOnClickListener {
            val intent = Intent(this, BuildPage::class.java)
            startActivity(intent)
        }

        account.setOnClickListener {
            val intent = Intent(this, AccountPage::class.java)
            intent.putExtra("USER_EMAIL", email) // 로그인 시 입력된 이메일
            startActivity(intent)
        }

        review.setOnClickListener {
            val intent = Intent(this, ReviewPage::class.java)
            intent.putExtra("USER_ID", userId)
            val name = email?.let { it1 -> extractEmailPrefix(it1) }
            intent.putExtra("USER_NAME", name)
            startActivity(intent)
        }

        setting.setOnClickListener {
            val intent = Intent(this, SettingPage::class.java)
            startActivity(intent)
        }

        support.setOnClickListener {
            val intent = Intent(this, SupportPage::class.java)
            startActivity(intent)
        }

        notice.setOnClickListener {
            val intent = Intent(this, NoticePage::class.java)
            startActivity(intent)
        }

        build_button.setOnClickListener {
            val intent = Intent(this, BuildPage::class.java)
            startActivity(intent)
        }

        lowbarHome.setOnClickListener {
            val intent = Intent(this, MainPage::class.java)
            startActivity(intent)
        }

        lowbarBuild.setOnClickListener {
            val intent = Intent(this, BuildPage::class.java)
            startActivity(intent)
        }

        lowbarSetting.setOnClickListener {
            val intent = Intent(this, SettingPage::class.java)
            startActivity(intent)
        }
    }

}