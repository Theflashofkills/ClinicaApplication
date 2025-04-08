package com.thiagojunhonma.devhealthy

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.thiagojunhonma.devhealthy.databinding.ActivityCadastroPacienteBinding
import java.util.*

class CadastroPacienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPacienteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroPacienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbarCadastro
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Cadastro de Paciente"

        toolbar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        // Dropdown de sexo
        val sexoOptions = listOf("Masculino", "Feminino", "Outro")
        val adapterSexo = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sexoOptions)
        binding.etSexo.setAdapter(adapterSexo)

        // Mostra o menu ao clicar no campo
        binding.etSexo.setOnClickListener {
            binding.etSexo.showDropDown()
        }

        // Data de nascimento: permite digitar e também escolher no calendário
        binding.etDataNascimento.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.etDataNascimento.text.isNullOrEmpty()) {
                mostrarDatePicker()
            }
        }

        binding.etDataNascimento.setOnClickListener {
            mostrarDatePicker()
        }

        // Ação do botão cadastrar
        binding.btnCadastrar.setOnClickListener {
            val nome = binding.etNome.text.toString()
            val cpf = binding.etCpf.text.toString()
            val dataNascimento = binding.etDataNascimento.text.toString()
            val sexo = binding.etSexo.text.toString()
            val telefone = binding.etTelefone.text.toString()
            val endereco = binding.etEndereco.text.toString()

            Toast.makeText(this, "Paciente cadastrado: $nome", Toast.LENGTH_SHORT).show()
            // Aqui você pode salvar os dados no banco
        }
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val ano = calendar.get(Calendar.YEAR)
        val mes = calendar.get(Calendar.MONTH)
        val dia = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val dataFormatada = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etDataNascimento.setText(dataFormatada)
            },
            ano, mes, dia
        )
        datePicker.show()
    }
}
