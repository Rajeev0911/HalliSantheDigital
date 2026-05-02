package com.halliSanthe.app.utils

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiHelper {

    // IMPORTANT: Replace with your actual Gemini API Key
    private const val API_KEY = "AIzaSyDqAqM0xVrAZuyelMpAvgZhVdZyIZP7qGk"

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY
    )

    suspend fun generateDescription(productName: String, category: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = "Generate a short, attractive, and traditional-sounding description (max 2-3 sentences) for a product named '$productName' in the '$category' category for an Indian artisan market app called Halli-Santhe."
                
                val response = model.generateContent(prompt)
                response.text
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
