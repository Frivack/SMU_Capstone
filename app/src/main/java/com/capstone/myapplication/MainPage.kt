package com.capstone.myapplication

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainpage)
    }

    private fun setupSwichButtons()
    {
        var build = findViewById<ImageView>(R.id.img_newbuild)
        var account = findViewById<ImageView>(R.id.img_account)
        var review = findViewById<ImageView>(R.id.img_review)
        var setting = findViewById<ImageView>(R.id.img_setting)
        var support = findViewById<ImageView>(R.id.img_support)
        var notice = findViewById<ImageView>(R.id.img_notice)
    }

}