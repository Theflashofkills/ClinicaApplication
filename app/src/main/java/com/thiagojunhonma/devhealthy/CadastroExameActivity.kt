package com.thiagojunhonma.devhealthy

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.couchbase.lite.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import java.util.*

class CadastroExameActivity : AppCompatActivity() {

    private val PERMISSAO_GALERIA = 200
    private val REQUISICAO_IMAGEM = 100

    private lateinit var autoCompletePaciente: AutoCompleteTextView
    private lateinit var editNomePaciente: EditText
    private lateinit var editCpfPaciente: EditText
    private lateinit var imagePreview: ImageView
    private lateinit var btnSelecionarImagem: Button
    private lateinit var btnSalvarExame: Button
    private lateinit var editDetalhesExame: EditText

    private var imageUri: Uri? = null
    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var database: Database
    private val pacientesMap = mutableMapOf<String, Pair<String, String>>() // Nome → (Nome, CPF)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_exame)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarCadastroExame)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Cadastro Pedido de Exame"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Inicializar componentes
        autoCompletePaciente = findViewById(R.id.autoCompletePaciente)
        editNomePaciente = findViewById(R.id.editNomePaciente)
        editCpfPaciente = findViewById(R.id.editCpfPaciente)
        imagePreview = findViewById(R.id.imagePreview)
        btnSelecionarImagem = findViewById(R.id.btnSelecionarImagem)
        btnSalvarExame = findViewById(R.id.btnSalvarExame)
        editDetalhesExame = findViewById(R.id.editDetalhesExame)

        // Inicializar banco local Couchbase Lite
        CouchbaseLite.init(applicationContext)
        val config = DatabaseConfigurationFactory.create()
        database = Database("devhealthy", config)

        carregarPacientes()

        autoCompletePaciente.setOnItemClickListener { parent, _, position, _ ->
            val nomeSelecionado = parent.getItemAtPosition(position).toString()
            val dados = pacientesMap[nomeSelecionado]
            if (dados != null) {
                editNomePaciente.setText(dados.first)
                editCpfPaciente.setText(dados.second)
            }
        }

        btnSelecionarImagem.setOnClickListener {
            solicitarPermissaoGaleria()
        }

        btnSalvarExame.setOnClickListener {
            salvarExame()
        }
    }

    private fun carregarPacientes() {
        val query = QueryBuilder.select(SelectResult.property("nome"), SelectResult.property("cpf"))
            .from(DataSource.database(database))
            .where(Expression.property("type").equalTo(Expression.string("paciente")))

        val nomes = mutableListOf<String>()

        try {
            val resultSet = query.execute()
            for (result in resultSet) {
                val nome = result.getString("nome") ?: continue
                val cpf = result.getString("cpf") ?: continue
                nomes.add(nome)
                pacientesMap[nome] = Pair(nome, cpf)
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nomes)
            autoCompletePaciente.setAdapter(adapter)

        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun solicitarPermissaoGaleria() {
        val permissao = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permissao) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permissao), PERMISSAO_GALERIA)
        } else {
            abrirGaleria()
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, REQUISICAO_IMAGEM)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSAO_GALERIA && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirGaleria()
        } else {
            Toast.makeText(this, "Permissão negada para acessar imagens", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUISICAO_IMAGEM && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            if (imageUri != null) {
                contentResolver.takePersistableUriPermission(
                    imageUri!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                imagePreview.setImageURI(imageUri)
            }
        }
    }

    private fun salvarExame() {
        val nome = editNomePaciente.text.toString()
        val cpf = editCpfPaciente.text.toString()
        val detalhes = editDetalhesExame.text.toString()

        if (nome.isEmpty() || cpf.isEmpty() || detalhes.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Preencha todos os campos e selecione uma imagem", Toast.LENGTH_SHORT).show()
            return
        }

        val nomeArquivoSeguro = nome.replace("\\s".toRegex(), "_") + "_" + System.currentTimeMillis() + ".jpg"
        val imageRef = storage.reference.child("exames/$nomeArquivoSeguro")

        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .build()

        imageRef.putFile(imageUri!!, metadata)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val exame = hashMapOf(
                        "nomePaciente" to nome,
                        "cpfPaciente" to cpf,
                        "detalhes" to detalhes,
                        "imagemUrl" to uri.toString()
                    )
                    firestore.collection("exames").add(exame)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Exame cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro ao salvar no Firestore: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao fazer upload: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
