package com.example.clashp.repository

import com.example.clashp.model.Question
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class QuestionRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val questionsCollection = firestore.collection("questions")

    // obtener preguntas por categoría
    suspend fun getQuestionsByCategory(categoryName: String, limit: Int = 4): Result<List<Question>> {
        return try {
            val snapshot = questionsCollection
                .whereEqualTo("category", categoryName)
                .get()
                .await()

            val allQuestions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Question::class.java)
            }.map { question ->
                question.copy(options = question.options.shuffled())
            }

            if (allQuestions.size >= limit) {
                Result.success(allQuestions.shuffled().take(limit))
            } else {
                Result.failure(Exception("No hay suficientes preguntas para la categoría $categoryName"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener todas las preguntas
    suspend fun getAllQuestions(): Result<List<Question>> {
        return try {
            val snapshot = questionsCollection.get().await()
            val questions = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Question::class.java)
            }.map { question ->
                // mezclar para que nosea siempre igual
                question.copy(options = question.options.shuffled())
            }
            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Añadir pregunta
    suspend fun addQuestion(question: Question): Result<String> {
        return try {
            val data = hashMapOf(
                "text" to question.text,
                "options" to question.options,
                "correctAnswer" to question.correctAnswer,
                "category" to question.category
            )

            val docRef = questionsCollection.add(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
