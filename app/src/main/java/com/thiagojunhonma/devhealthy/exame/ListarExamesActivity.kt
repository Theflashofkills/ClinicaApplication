package com.thiagojunhonma.devhealthy.exame

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.thiagojunhonma.devhealthy.ExamesAdapter
import com.thiagojunhonma.devhealthy.databinding.ActivityListarExamesBinding

class ListarExamesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListarExamesBinding
    private lateinit var adapter: ExamesAdapter
    private val listaExames = mutableListOf<Exame>()

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListarExamesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ExamesAdapter(listaExames)
        binding.recyclerViewExames.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewExames.adapter = adapter

        carregarExamesDoFirestore()
    }

    private fun carregarExamesDoFirestore() {
        firestore.collection("exames")
            .get()
            .addOnSuccessListener { documentos ->
                listaExames.clear()

                if (documentos.isEmpty) {
                    Log.d("ListarExames", "Nenhum exame encontrado")
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                val totalDocs = documentos.size()
                var carregados = 0

                for (doc in documentos) {
                    val nome = doc.getString("nomePaciente") ?: "Sem nome"
                    val cpf = doc.getString("cpfPaciente") ?: "Sem CPF"
                    val fotoPath = doc.getString("fotoExamePath")

                    if (fotoPath != null) {
                        storage.reference.child(fotoPath).downloadUrl
                            .addOnSuccessListener { uri ->
                                listaExames.add(Exame(nome, cpf, uri.toString()))
                                carregados++
                                if (carregados == totalDocs) {
                                    adapter.notifyDataSetChanged()
                                }
                            }
                            .addOnFailureListener {
                                listaExames.add(Exame(nome, cpf, null))
                                carregados++
                                if (carregados == totalDocs) {
                                    adapter.notifyDataSetChanged()
                                }
                            }
                    } else {
                        listaExames.add(Exame(nome, cpf, null))
                        carregados++
                        if (carregados == totalDocs) {
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ListarExames", "Erro ao carregar exames: ", e)
            }
    }
}
