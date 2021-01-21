package de.Jan.SlashCommands

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class Interaction(private val interactionToken: String, private  val token: String, private val id: String, private val interactionID: String) {

    private val url = "https://discord.com/api/v8/interactions/$interactionID/$interactionToken/callback"
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()


    fun callback(type: Int, value: String? = null) {
        println(url)
        val i = JSONObject()
        i.put("type", type)
        if(value != null) {
            val data = JSONObject()
                    .put("tts", false)
                    .put("content", value)
                    .put("embeds", JSONArray())
                    .put("allowed_mentions", JSONArray())
            i.put("data", data)
        }
        val r = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bot $token")
                .post(i.toString().toRequestBody(JSON))
        val call = client.newCall(r.build()).execute()
        println(call.body!!.string())
    }

}