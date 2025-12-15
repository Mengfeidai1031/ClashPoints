package com.clashpoints.app.repository

import android.util.Log
import com.clashpoints.app.data.RankingEntry
import com.google.firebase.database.*

class FirebaseRepository {
    
    private val database: FirebaseDatabase
    private val rankingRef: DatabaseReference
    
    init {
        try {
            database = FirebaseDatabase.getInstance()
            rankingRef = database.getReference("ranking")
            
            Log.d("FirebaseRepository", "Firebase Repository initialized successfully")
        } catch (e: Exception) {
            Log.e("FirebaseRepository", "Error initializing Firebase Repository", e)
            throw e
        }
    }
    
    fun saveScore(name: String, points: Int, onComplete: (Boolean) -> Unit) {
        // Primero obtener el top 10 actual
        rankingRef.orderByChild("points")
            .limitToLast(10)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries = mutableListOf<RankingEntry>()
                    
                    snapshot.children.forEach { childSnapshot ->
                        val entry = childSnapshot.getValue(RankingEntry::class.java)
                        entry?.let {
                            entries.add(it.copy(key = childSnapshot.key ?: ""))
                        }
                    }
                    
                    // Ordenar por puntos descendente
                    val sortedEntries = entries.sortedByDescending { it.points }
                    
                    // Verificar si la nueva puntuación entra en el top 10
                    if (sortedEntries.size < 10 || points > sortedEntries.lastOrNull()?.points ?: 0) {
                        // Crear nueva entrada
                        val newEntryRef = rankingRef.push()
                        val newEntry = RankingEntry(
                            name = name,
                            points = points
                        )
                        
                        newEntryRef.setValue(newEntry).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Si hay más de 10 entradas, eliminar las peores
                                checkAndRemoveExtraEntries()
                                onComplete(true)
                            } else {
                                onComplete(false)
                            }
                        }
                    } else {
                        // La puntuación no entra en el top 10
                        onComplete(true)
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    onComplete(false)
                }
            })
    }
    
    private fun checkAndRemoveExtraEntries() {
        rankingRef.orderByChild("points")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries = mutableListOf<Pair<String, RankingEntry>>()
                    
                    snapshot.children.forEach { childSnapshot ->
                        val entry = childSnapshot.getValue(RankingEntry::class.java)
                        val key = childSnapshot.key
                        if (entry != null && key != null) {
                            entries.add(Pair(key, entry))
                        }
                    }
                    
                    // Ordenar por puntos descendente
                    val sortedEntries = entries.sortedByDescending { it.second.points }
                    
                    // Si hay más de 10, eliminar las peores
                    if (sortedEntries.size > 10) {
                        val entriesToRemove = sortedEntries.drop(10)
                        entriesToRemove.forEach { (key, _) ->
                            rankingRef.child(key).removeValue()
                        }
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    // Error al limpiar entradas extras
                }
            })
    }
    
    fun getRanking(onResult: (List<RankingEntry>) -> Unit) {
        rankingRef.orderByChild("points")
            .limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries = mutableListOf<RankingEntry>()
                    
                    snapshot.children.forEach { childSnapshot ->
                        val entry = childSnapshot.getValue(RankingEntry::class.java)
                        entry?.let {
                            entries.add(it.copy(key = childSnapshot.key ?: ""))
                        }
                    }
                    
                    // Ordenar por puntos descendente
                    onResult(entries.sortedByDescending { it.points })
                }
                
                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }
}
