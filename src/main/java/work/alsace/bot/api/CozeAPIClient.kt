package work.alsace.bot.api

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.io.IOException

class CozeAPIClient(private val accessToken: String) {
    private val gson: Gson = Gson()

    @Throws(IOException::class)
    fun sendMessage(
        botId: String?,
        userId: String?,
        query: String?,
        stream: Boolean,
        chatHistory: List<Message?>?
    ): ApiResponse {
        val httpClient = HttpClients.createDefault()
        val post: HttpPost = HttpPost(API_URL)
        post.setHeader("Authorization", "Bearer $accessToken")
        post.setHeader("Content-Type", "application/json")
        post.setHeader("Accept", "*/*")
        post.setHeader("Host", "api.coze.cn")
        post.setHeader("Connection", "keep-alive")
        val requestBody: RequestBody =
            RequestBody(botId, userId, query, stream, chatHistory)
        val entity = StringEntity(gson.toJson(requestBody), "UTF-8")
        post.entity = entity
        httpClient.execute(post).use { response ->
            val jsonResponse = EntityUtils.toString(response.entity, "UTF-8")
            return gson.fromJson(jsonResponse, ApiResponse::class.java)
        }
    }

    private class RequestBody(
        @field:SerializedName("bot_id") private val botId: String?, @field:SerializedName(
            "user"
        ) private val userId: String?, @field:SerializedName("query") private val query: String?, @field:SerializedName(
            "stream"
        ) private val stream: Boolean, @field:SerializedName("chat_history") private val chatHistory: List<Message?>?
    )

    fun getAnswerContent(response: ApiResponse): String? {
        for (message in response.messages!!) {
            if ("answer" == message.type) {
                return message.content
            }
        }
        return null
    }

    class ApiResponse {
        @SerializedName("messages")
        var messages: List<Message>? = null

        @SerializedName("conversation_id")
        var conversationId: String? = null

        @SerializedName("code")
        var code = 0

        @SerializedName("msg")
        var message: String? = null
    }

    class Message {
        @SerializedName("role")
        var role: String? = null

        @SerializedName("type")
        var type: String? = null

        @SerializedName("content")
        var content: String? = null

        @SerializedName("content_type")
        var contentType: String? = null
    }

    companion object {
        private const val API_URL = "https://api.coze.cn/open_api/v2/chat"
    }
}
