// Utilities.kt

package com.byteflipper.jsoncomposer

import org.json.JSONArray
import org.json.JSONObject
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.io.readText

object JsonParser {
    private const val TAG = "JsonParser"

    suspend fun parseJson(source: JsonSource): JsonParseResult {
        return try {
            val jsonString = when (source) {
                is JsonSource.Text -> source.jsonString
                is JsonSource.File -> source.file.toString()
                is JsonSource.Url -> fetchUrlContent(source.url)
            }

            val parsedData = try {
                JSONObject(jsonString)
            } catch (e: Exception) {
                try {
                    JSONArray(jsonString)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse JSON", e)
                    return JsonParseResult.Error("Invalid JSON format", e)
                }
            }

            Log.d(TAG, "JSON parsed successfully")
            JsonParseResult.Success(parsedData)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON source", e)
            JsonParseResult.Error("Failed to read JSON source", e)
        }
    }

    private suspend fun fetchUrlContent(url: URL): String = withContext(Dispatchers.IO) {
        val connection = url.openConnection() as HttpURLConnection
        try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
                    reader.readText()
                }
            } else {
                throw Exception("HTTP error code: ${connection.responseCode}")
            }
        } finally {
            connection.disconnect()
        }
    }
}