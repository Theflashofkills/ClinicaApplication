package com.thiagojunhonma.devhealthy

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.thiagojunhonma.devhealthy.databinding.ActivityCadastroPacienteBinding
import com.thiagojunhonma.devhealthy.db.DatabaseManager
import java.util.*

class CadastroPacienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPacienteBinding
    private lateinit var db: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroPacienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseManager(this)

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

        binding.etSexo.setOnClickListener {
            binding.etSexo.showDropDown()
        }

        // Date picker para data de nascimento
        binding.etDataNascimento.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.etDataNascimento.text.isNullOrEmpty()) {
                mostrarDatePicker()
            }
        }

        binding.etDataNascimento.setOnClickListener {
            mostrarDatePicker()
        }

        // Botão de cadastrar paciente
        binding.btnCadastrar.setOnClickListener {
            val nome = binding.etNome.text.toString()
            val cpf = binding.etCpf.text.toString()
            val dataNascimento = binding.etDataNascimento.text.toString()
            val sexo = binding.etSexo.text.toString()
            val telefone = binding.etTelefone.text.toString()
            val endereco = binding.etEndereco.text.toString()

            if (nome.isBlank() || cpf.isBlank()) {
                Toast.makeText(this, "Preencha os campos obrigatórios!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Mapeia os dados do paciente para um formato que o Couchbase Lite compreenda
            val paciente = mapOf(
                "nome" to nome,
                "cpf" to cpf,
                "dataNascimento" to dataNascimento,
                "sexo" to sexo,
                "telefone" to telefone,
                "endereco" to endereco,
                "type" to "paciente"
            )

            // Salva o paciente no Couchbase Lite
            db.salvarPaciente(paciente)

            Toast.makeText(this, "Paciente cadastrado com sucesso!", Toast.LENGTH_SHORT).show()

            // Retorna para tela inicial
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
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
