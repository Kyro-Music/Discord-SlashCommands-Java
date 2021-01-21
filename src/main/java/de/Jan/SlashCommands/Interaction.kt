package de.Jan.SlashCommands

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.Webhook
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class Interaction(private val builder: SlashCommandBuilder, private val interactionToken: String, private  val token: String, private val id: String, private val interactionID: String) {

    private val callback_url = "https://discord.com/api/v8/interactions/$interactionID/$interactionToken/callback"
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()


    fun callback(type: Int, value: String? = null) {
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
                .url(callback_url)
                .addHeader("Authorization", "Bot $token")
                .post(i.toString().toRequestBody(JSON))
        val call = client.newCall(r.build()).execute()
        builder.checkIfError(call.body!!.string())
    }

    fun sendMessage(value: String) {
        val createURL = "https://discord.com/api/v8/webhooks/${builder.botID}/$interactionToken"
            val data = JSONObject()
                    .put("tts", false)
                    .put("content", value)
                    .put("embeds", JSONArray())
                    .put("allowed_mentions", JSONArray())
        val r = Request.Builder()
                .url(createURL)
                .addHeader("Authorization", "Bot $token")
                .post(data.toString().toRequestBody(JSON))
        val call = client.newCall(r.build()).execute()
        builder.checkIfError(call.body!!.string())
    }

}