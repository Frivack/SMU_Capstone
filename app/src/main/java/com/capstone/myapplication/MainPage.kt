package com.capstone.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainpage)

        setupSwichButtons()
    }

    private fun setupSwichButtons()
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
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, BuildPage::class.java)
            startActivity(intent)
        }

        account.setOnClickListener {
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, AccountPage::class.java)
            startActivity(intent)
        }

        review.setOnClickListener {
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, ReviewPage::class.java)
            startActivity(intent)
        }

        setting.setOnClickListener {
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, SettingPage::class.java)
            startActivity(intent)
        }

        support.setOnClickListener {
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, SupportPage::class.java)
            startActivity(intent)
        }

        notice.setOnClickListener {
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, NoticePage::class.java)
            startActivity(intent)
        }

        build_button.setOnClickListener {
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, BuildPage::class.java)
            startActivity(intent)
        }

        lowbarHome.setOnClickListener {
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, MainPage::class.java)
            startActivity(intent)
        }

        lowbarBuild.setOnClickListener {
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, BuildPage::class.java)
            startActivity(intent)
        }

        lowbarSetting.setOnClickListener {
            // Intent를 사용해 NextActivity로 이동
            val intent = Intent(this, SettingPage::class.java)
            startActivity(intent)
        }
    }

}