package com.thiagojunhonma.devhealthy.db

import android.content.Context
import com.couchbase.lite.*

class DatabaseManager(context: Context) {

    private val database: Database

    init {
        // Inicializa o banco de dados
        val config = DatabaseConfiguration(context)
        database = Database("pacientes", config)
    }

    // Função para salvar um paciente
    fun salvarPaciente(paciente: Map<String, String>) {
        try {
            // Cria um documento mutável
            val doc = MutableDocument()

            // Preenche os campos do paciente no documento
            paciente.forEach { (key, value) ->
                doc.setString(key, value)
            }

            // Salva o documento no banco de dados
            database.save(doc)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
