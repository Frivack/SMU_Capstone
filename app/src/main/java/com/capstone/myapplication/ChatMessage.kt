package com.capstone.myapplication

data class ChatMessage (
    val message: String,
    val isUser: Boolean // 사용자가 보낸 메시지인지, 봇이 보낸 메시지인지 구분
)