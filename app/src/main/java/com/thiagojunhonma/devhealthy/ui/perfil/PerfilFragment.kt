package com.thiagojunhonma.devhealthy.ui.perfil

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.thiagojunhonma.devhealthy.LoginActivity
import com.thiagojunhonma.devhealthy.databinding.FragmentPerfilBinding

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

    // Registrar o launcher para abrir a galeria e receber a Uri da imagem selecionada
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Mostrar a imagem local imediatamente
            Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(binding.imagePerfil)

            // Fazer upload da imagem para o Firebase Storage
            uploadImagemPerfil(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val root = binding.root

        carregarDadosUsuario()

        binding.sairConta.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.excluirConta.setOnClickListener {
            confirmarExclusaoConta()
        }

        binding.containerFotoPerfil.setOnClickListener {
            abrirGaleria()
        }

        return root
    }

    private fun carregarDadosUsuario() {
        // Exemplo estático, substitua com dados reais do seu banco
        val nomeMedico = "Dr. Thiago Honma"
        val crmMedico = "CRM: 123456-SP"

        binding.txtNomeMedico.text = nomeMedico
        binding.txtCrmMedico.text = crmMedico

        // Carregar imagem do Firebase Storage
        val perfilRef = storageRef.child("medicos/${auth.currentUser?.uid}/perfil.jpg")
        perfilRef.downloadUrl.addOnSuccessListener { uri: Uri ->
            Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(binding.imagePerfil)
        }.addOnFailureListener {
            // Se não tiver imagem, exibe ícone padrão
            binding.imagePerfil.setImageResource(com.thiagojunhonma.devhealthy.R.drawable.assistencia_medica)
        }
    }

    private fun abrirGaleria() {
        pickImageLauncher.launch("image/*")
    }

    private fun uploadImagemPerfil(uri: Uri) {
        val perfilRef = storageRef.child("medicos/${auth.currentUser?.uid}/perfil.jpg")

        perfilRef.putFile(uri)
            .addOnSuccessListener {
                // Opcional: mostrar mensagem de sucesso
            }
            .addOnFailureListener {
                // Opcional: mostrar mensagem de erro
            }
    }

    private fun confirmarExclusaoConta() {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Conta")
            .setMessage("Tem certeza que deseja excluir sua conta? Esta ação é irreversível.")
            .setPositiveButton("Excluir") { dialog, _ ->
                excluirConta()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun excluirConta() {
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // Exibir mensagem de erro, se desejar
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
