package com.capstone.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.capstone.ChatRequest
import com.example.capstone.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

// 앱 전체에서 채팅 기록과 통신을 관리하는 싱글톤 객체
object ChatHistoryManager {

    private val _messageList = MutableLiveData<List<ChatMessage>>(emptyList())
    val messageList: LiveData<List<ChatMessage>> = _messageList

    fun sendMessageToServer(messageText: String) {
        // --- 1. 사용자 메시지 추가 ---
        val userMessage = ChatMessage(messageText, true)
        // 기존 리스트에 새 메시지를 더해 "새로운 리스트"를 만듦
        _messageList.value = _messageList.value.orEmpty() + userMessage

        // --- 2. 서버 통신 ---
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = ChatRequest(prompt = messageText)
                val response = RetrofitClient.apiService.sendMessageToChatbot(request)

                val botMessageText = if (response.isSuccessful && response.body() != null) {
                    response.body()!!.response
                } else {
                    "서버 응답 실패: ${response.code()}"
                }

                // --- 3. 봇 메시지 추가 (메인 스레드에서) ---
                withContext(Dispatchers.Main) {
                    val botMessage = ChatMessage(botMessageText, false)
                    // 기존 리스트에 봇 메시지를 더해 "또 다른 새로운 리스트"를 만듦
                    _messageList.value = _messageList.value.orEmpty() + botMessage
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // --- 4. 오류 메시지 추가 (메인 스레드에서) ---
                withContext(Dispatchers.Main) {
                    val errorMessage = ChatMessage("오류 발생: ${e.message}", false)
                    // 기존 리스트에 오류 메시지를 더해 "또 다른 새로운 리스트"를 만듦
                    _messageList.value = _messageList.value.orEmpty() + errorMessage
                }
            }
        }
    }
}