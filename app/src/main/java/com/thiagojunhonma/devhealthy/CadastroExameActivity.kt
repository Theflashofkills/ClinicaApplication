package com.thiagojunhonma.devhealthy

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.thiagojunhonma.devhealthy.databinding.ActivityCriarExameBinding

class CadastroExameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCriarExameBinding
    private var imageUri: Uri? = null
    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCriarExameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar com botão de voltar
        val toolbar: Toolbar = findViewById(R.id.toolbarCadastroExame)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Cadastro de Exame"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Botão para selecionar imagem
        binding.btnSelecionarImagem.setOnClickListener {
            solicitarPermissaoGaleria()
        }

        // Botão para salvar exame
        binding.btnSalvarExame.setOnClickListener {
            val nome = binding.editNomePaciente.text.toString()
            val cpf = binding.editCpfPaciente.text.toString()
            val detalhes = binding.editDetalhesExame.text.toString()

            if (nome.isEmpty() || cpf.isEmpty() || detalhes.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Preencha todos os campos e selecione uma imagem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imageRef = storage.reference.child("exames/${System.currentTimeMillis()}.jpg")
            imageRef.putFile(imageUri!!)
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
                                e.printStackTrace()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao fazer upload: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
        }
    }

    // Permissão e seleção de imagem
    private fun solicitarPermissaoGaleria() {
        val permissao = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permissao) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permissao), 200)
        } else {
            abrirGaleria()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 200 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            abrirGaleria()
        } else {
            Toast.makeText(this, "Permissão negada para acessar imagens", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.imagePreview.setImageURI(imageUri)
        }
    }
}
