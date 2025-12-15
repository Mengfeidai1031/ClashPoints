package com.clashpoints.app.repository

import android.content.Context
import android.util.Log
import com.clashpoints.app.R
import com.clashpoints.app.data.Question
import com.clashpoints.app.data.QuestionsData
import com.google.gson.Gson
import java.io.InputStreamReader

class QuestionRepository(private val context: Context) {
    
    private var allQuestions: List<Question> = emptyList()
    
    init {
        loadQuestions()
    }
    
    private fun loadQuestions() {
        try {
            val inputStream = context.resources.openRawResource(R.raw.questions)
            val reader = InputStreamReader(inputStream)
            val questionsData = Gson().fromJson(reader, QuestionsData::class.java)
            allQuestions = questionsData.questions
            reader.close()
            Log.d("QuestionRepository", "Loaded ${allQuestions.size} questions")
        } catch (e: Exception) {
            Log.e("QuestionRepository", "Error loading questions", e)
            e.printStackTrace()
        }
    }
    
    fun getQuestionsByCategory(category: String, count: Int = 4): List<Question> {
        val categoryQuestions = allQuestions.filter { it.category == category }
        return if (categoryQuestions.size >= count) {
            categoryQuestions.shuffled().take(count)
        } else {
            categoryQuestions.shuffled()
        }
    }
    
    fun getAllCategories(): List<String> {
        return listOf("Geografia", "Ciencia", "Historia", "Deportes", "Arte", "Entretenimiento")
    }
}
