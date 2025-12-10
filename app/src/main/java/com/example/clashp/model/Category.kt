package com.example.clashp.model

enum class Category(val displayName: String) {
    HISTORIA("Historia"),
    DEPORTES("Deportes"),
    ARTE("Arte"),
    GEOGRAFIA("Geografía"),
    ENTRETENIMIENTO("Entretenimiento"),
    CIENCIA("Ciencia");

    companion object {
        fun random(): Category = entries.random()
    }
}
