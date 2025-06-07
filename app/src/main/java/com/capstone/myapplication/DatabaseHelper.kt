package com.example.capstone

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

fun sha256(input: String): String {
    val bytes = java.security.MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}

// 챗봇 서버에 보낼 요청 데이터 형식
data class ChatRequest(val prompt: String)

// 챗봇 서버로부터 받을 응답 데이터 형식
data class ChatResponse(val response: String)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val phone: String
)

data class Review(
    val review_id: Int,
    val user_id: Int,
    val title: String,
    val content: String,
    val created_at: String,
    val thumbs_up: Int?,
    val comments_count: Int?
)
data class LoginRequest(
    val email: String,
    val password: String
)
data class LoginResponse(
    val message: String,
    val user: UserData?
)

data class UserData(
    val user_id: Int,
    val email: String
)

data class AddReviewRequest(
    val userId: Int,
    val title: String,
    val content: String
)
data class BasicResponse(
    val message: String
)

interface ApiService {
    @POST("register")
    suspend fun register(@Body body: RegisterRequest): retrofit2.Response<Map<String, String>>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("reviews")
    suspend fun getAllReviews(): Response<List<Review>>

    @POST("reviews")
    suspend fun addReview(@Body review: AddReviewRequest): Response<BasicResponse>

    @GET("all-parts")
    suspend fun getAllParts(
        @Query("theme") theme: Int?, // 테마 (null 가능)
        @Query("limit") limit: Int,  // 가져올 개수
        @Query("offset") offset: Int // 시작 위치
    ): Response<Map<String, List<Map<String, Any>>>>
    @POST("/botapi/chat")
    suspend fun sendMessageToChatbot(@Body request: ChatRequest): Response<ChatResponse>
}

object RetrofitClient {
    private const val BASE_URL = "http://134.185.108.94/db/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS) // 서버 연결에 걸리는 시간
        .readTimeout(60, TimeUnit.SECONDS)    // 연결 후 응답을 기다리는 시간 (가장 중요)
        .writeTimeout(60, TimeUnit.SECONDS)   // 요청을 서버로 보내는 시간
        .build()

    val apiService: ApiService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}


class ApiHelper {
    suspend fun register(username: String, email: String, password: String, phone: String): Boolean {
        val passwordHash = sha256(password) // << 여기 추가!
        val body = RegisterRequest(username, email, passwordHash, phone)
        val response = RetrofitClient.apiService.register(body)
        return response.isSuccessful && response.body()?.get("message") == "Registration successful"
    }

    suspend fun login(email: String, password: String): LoginResponse? {
        val passwordHash = sha256(password) // 비밀번호 해시화
        val response = RetrofitClient.apiService.login(LoginRequest(email, passwordHash))
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun fetchAllReviews(): List<Review> {
        val response = RetrofitClient.apiService.getAllReviews()
        return if (response.isSuccessful) response.body() ?: emptyList() else emptyList()
    }
    suspend fun insertReview(userId: Int, title: String, content: String): Boolean {
        val body = AddReviewRequest(userId, title, content)
        val response = RetrofitClient.apiService.addReview(body)
        return response.isSuccessful && response.body()?.message == "Review added"
    }
    suspend fun fetchAllParts(theme: Int? = null, limit: Int = 10, offset: Int = 0): Map<String, List<Map<String, Any>>> {
        val response = RetrofitClient.apiService.getAllParts(theme, limit, offset)
        return if (response.isSuccessful) response.body() ?: emptyMap() else emptyMap()
    }
}
