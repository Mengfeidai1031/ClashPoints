package com.clashpoints.app

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class ClashPointsApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        try {
            // Inicializar Firebase
            FirebaseApp.initializeApp(this)
            
            // Configurar Firebase Database
            val database = FirebaseDatabase.getInstance()
            
            // Habilitar persistencia offline (solo una vez)
            try {
                database.setPersistenceEnabled(true)
            } catch (e: Exception) {
                Log.w("ClashPointsApp", "Persistence already enabled", e)
            }
            
            Log.d("ClashPointsApp", "Application initialized successfully")
        } catch (e: Exception) {
            Log.e("ClashPointsApp", "Error initializing application", e)
        }
    }
}
