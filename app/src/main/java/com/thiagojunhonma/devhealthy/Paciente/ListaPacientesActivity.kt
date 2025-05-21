package com.thiagojunhonma.devhealthy.Paciente

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.couchbase.lite.*
import com.thiagojunhonma.devhealthy.databinding.ActivityListaPacientesBinding
import android.util.Log
import androidx.core.view.WindowCompat

class ListaPacientesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaPacientesBinding
    private lateinit var database: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaPacientesBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Pacientes"

        // Inicialização do banco Couchbase Lite
        CouchbaseLite.init(applicationContext)

        val config = DatabaseConfiguration()
        database = Database("devhealthy", config) // Nome padronizado

        // Buscar pacientes do banco e exibir
        val pacientes = getPacientesFromDatabase()
        val nomesPacientes = pacientes.map { it["nome"] + " - CPF: " + it["cpf"] }
        Log.d("ListaPacientes", "Total de pacientes: ${nomesPacientes.size}")
        nomesPacientes.forEach { Log.d("ListaPacientes", it) }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nomesPacientes)
        binding.listViewPacientes.adapter = adapter

        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

    }

    private fun getPacientesFromDatabase(): List<Map<String, String>> {
        val pacientes = mutableListOf<Map<String, String>>()

        val query = QueryBuilder.select(
            SelectResult.property("nome"),
            SelectResult.property("cpf")
        )
            .from(DataSource.database(database))
            .where(Expression.property("type").equalTo(Expression.string("paciente")))

        try {
            val resultSet = query.execute()
            for (result in resultSet) {
                val nome = result.getString("nome")
                val cpf = result.getString("cpf")
                if (nome != null && cpf != null) {
                    Log.d("ListaPacientes", "Paciente encontrado: nome=$nome, cpf=$cpf")
                    pacientes.add(mapOf("nome" to nome, "cpf" to cpf))
                }
            }
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }

        return pacientes
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }
}
