package com.thiagojunhonma.devhealthy.Database

import android.content.Context
import com.couchbase.lite.*
import java.util.UUID

class DatabaseManager(context: Context) {

    private val database: Database

    init {
        CouchbaseLite.init(context) // Adicionado para garantir inicialização
        val config = DatabaseConfiguration()
        database = Database("devhealthy", config) // Nome padronizado
    }

    // Função para salvar um paciente
    fun salvarPaciente(paciente: Map<String, String>) {
        try {
            val cpfSemMascara = paciente["cpf"]?.replace(Regex("[^\\d]"), "") ?: UUID.randomUUID().toString()
            val doc = MutableDocument("paciente::$cpfSemMascara")
            paciente.forEach { (key, value) ->
                doc.setString(key, value)
            }
            database.save(doc)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Função opcional para buscar pacientes
    fun buscarTodosPacientes(): List<Map<String, String>> {
        val pacientes = mutableListOf<Map<String, String>>()
        val query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.database(database))
            .where(Expression.property("type").equalTo(Expression.string("paciente")))

        try {
            val result = query.execute()
            for (row in result) {
                val dict = row.getDictionary("devhealthy")
                if (dict != null) {
                    val paciente = mutableMapOf<String, String>()
                    paciente["nome"] = dict.getString("nome") ?: "Desconhecido"
                    paciente["cpf"] = dict.getString("cpf") ?: "Desconhecido"
                    paciente["endereco"] = dict.getString("endereco") ?: "Desconhecido"
                    pacientes.add(paciente)
                }
            }
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }

        return pacientes
    }
}
