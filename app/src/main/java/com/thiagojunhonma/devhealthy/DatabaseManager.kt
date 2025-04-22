package com.thiagojunhonma.devhealthy.db

import android.content.Context
import com.couchbase.lite.*

class DatabaseManager(context: Context) {

    private val database: Database

    init {
        // Inicializa o banco de dados
        val config = DatabaseConfiguration()
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

    // Função para buscar todos os pacientes
    fun buscarTodosPacientes(): List<Map<String, String>> {
        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.database(database))

        val pacientes = mutableListOf<Map<String, String>>()

        try {
            // Executa a consulta e percorre os resultados
            val result = query.execute()
            for (row in result) {
                val paciente = mutableMapOf<String, String>()
                paciente["nome"] = row.getString("nome") ?: "Desconhecido"
                paciente["cpf"] = row.getString("cpf") ?: "Desconhecido"
                paciente["endereco"] = row.getDictionary("endereco")?.getString("rua") ?: "Desconhecido"
                pacientes.add(paciente)
            }
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }

        return pacientes
    }
}
