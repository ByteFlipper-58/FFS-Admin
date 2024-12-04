package com.byteflipper.ffsadmin.services

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.Base64

class GitHubService(
    private val token: String,
    private val owner: String,
    private val repo: String
) {
    private val client = OkHttpClient()
    private val baseUrl = "https://api.github.com/repos/$owner/$repo"

    // Get file content
    fun getFileContent(path: String): String? {
        val request = Request.Builder()
            .url("$baseUrl/contents/$path")
            .addHeader("Authorization", "token $token")
            .build()

        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null

            val jsonResponse = JSONObject(response.body?.string() ?: "")
            val content = jsonResponse.getString("content")
            String(Base64.getDecoder().decode(content))
        }
    }

    // Create or update file
    fun createOrUpdateFile(
        path: String,
        content: String,
        commitMessage: String
    ): Boolean {
        val encodedContent = Base64.getEncoder().encodeToString(content.toByteArray())

        val body = JSONObject().apply {
            put("message", commitMessage)
            put("content", encodedContent)
        }

        val request = Request.Builder()
            .url("$baseUrl/contents/$path")
            .put(body.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("Authorization", "token $token")
            .build()

        return client.newCall(request).execute().use { response ->
            response.isSuccessful
        }
    }

    // Delete file
    fun deleteFile(
        path: String,
        commitMessage: String
    ): Boolean {
        // First, get the current file's SHA
        val getRequest = Request.Builder()
            .url("$baseUrl/contents/$path")
            .addHeader("Authorization", "token $token")
            .build()

        val fileSha = client.newCall(getRequest).execute().use { response ->
            if (!response.isSuccessful) return false

            val jsonResponse = JSONObject(response.body?.string() ?: "")
            jsonResponse.getString("sha")
        }

        // Then delete the file
        val deleteBody = JSONObject().apply {
            put("message", commitMessage)
            put("sha", fileSha)
        }

        val deleteRequest = Request.Builder()
            .url("$baseUrl/contents/$path")
            .delete(deleteBody.toString().toRequestBody("application/json".toMediaTypeOrNull()))
            .addHeader("Authorization", "token $token")
            .build()

        return client.newCall(deleteRequest).execute().use { response ->
            response.isSuccessful
        }
    }
}