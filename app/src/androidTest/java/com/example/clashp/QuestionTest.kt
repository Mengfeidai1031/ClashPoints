package com.example.clashp

import com.example.clashp.model.Category
import com.example.clashp.model.Question
import org.junit.Assert.assertEquals
import org.junit.Test

class QuestionTest {

    @Test
    fun getCategoryEnum_mapsByDisplayNameIgnoringCase() {
        val q = Question(category = "geografía")
        assertEquals(Category.GEOGRAFIA, q.getCategoryEnum())
    }

    @Test
    fun getCategoryEnum_unknownCategory_fallsBackToHistoria() {
        val q = Question(category = "NoExiste")
        // tiene que salir historia porque puse historia por defecto si no sabe
        assertEquals(Category.HISTORIA, q.getCategoryEnum())
    }
}