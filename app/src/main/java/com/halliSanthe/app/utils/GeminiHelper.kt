package com.halliSanthe.app.utils

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiHelper {

    private const val API_KEY = ""

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY,
        systemInstruction = content { text("You are an expert copywriter for 'Halli-Santhe', an Indian artisan marketplace. Your job is to write short, evocative, and traditional descriptions for products. Use a warm, professional, and cultural tone.") }
    )

    suspend fun generateDescription(productName: String, category: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = "Generate a short, attractive, and traditional-sounding description (max 2 sentences) for a product named '$productName' in the '$category' category."
                
                val response = model.generateContent(prompt)
                response.text?.trim()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
