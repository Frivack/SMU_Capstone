package com.capstone.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatbotFragment : Fragment() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트의 레이아웃을 인플레이트
        return inflater.inflate(R.layout.fragment_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뷰 초기화
        recyclerViewChat = view.findViewById(R.id.recycler_view_chat)
        editTextMessage = view.findViewById(R.id.edit_text_message)
        buttonSend = view.findViewById(R.id.button_send)

        // 어댑터 및 RecyclerView 설정
        // ChatHistoryManager의 LiveData를 사용하여 초기 리스트를 가져옴
        chatAdapter = ChatAdapter(ChatHistoryManager.messageList.value.orEmpty().toMutableList())
        recyclerViewChat.layoutManager = LinearLayoutManager(context)
        recyclerViewChat.adapter = chatAdapter

        // '전송' 버튼 클릭 리스너
        buttonSend.setOnClickListener {
            val messageText = editTextMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                // ChatHistoryManager를 통해 메시지 전송
                ChatHistoryManager.sendMessageToServer(messageText)
                editTextMessage.text.clear()
            }
        }

        // LiveData를 관찰하여 채팅 목록에 변경이 생길 때마다 UI를 업데이트
        ChatHistoryManager.messageList.observe(viewLifecycleOwner) { messages ->
            // ChatAdapter의 updateMessages는 MutableList를 기대하므로 toMutableList()로 변환
            chatAdapter.updateMessages(messages.toMutableList())
            if (messages.isNotEmpty()) {
                recyclerViewChat.scrollToPosition(messages.size - 1)
            }
        }
    }
}