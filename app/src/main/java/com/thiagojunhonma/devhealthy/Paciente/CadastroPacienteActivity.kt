package com.thiagojunhonma.devhealthy.Paciente

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.thiagojunhonma.devhealthy.MainActivity
import com.thiagojunhonma.devhealthy.databinding.ActivityCadastroPacienteBinding
import com.thiagojunhonma.devhealthy.Database.DatabaseManager
import java.text.SimpleDateFormat
import java.util.*

class CadastroPacienteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroPacienteBinding
    private lateinit var db: DatabaseManager
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroPacienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseManager(this)

        val toolbar = binding.toolbarCadastro
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Cadastro de Paciente"

        toolbar.setNavigationOnClickListener {
            voltarParaMain()
        }

        // Dropdown de sexo
        val sexoOptions = listOf("Masculino", "Feminino", "Outro")
        val adapterSexo = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sexoOptions)
        binding.etSexo.setAdapter(adapterSexo)
        binding.etSexo.setOnClickListener {
            binding.etSexo.showDropDown()
        }

        // Máscara para CPF
        binding.etCpf.addTextChangedListener(cpfMaskWatcher)

        // Date picker para data de nascimento (bloqueia datas futuras)
        binding.etDataNascimento.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                mostrarDatePicker()
            }
        }

        binding.etDataNascimento.setOnClickListener {
            mostrarDatePicker()
        }

        // Botão cadastrar paciente
        binding.btnCadastrar.setOnClickListener {
            cadastrarPaciente()
        }
    }

    private fun cadastrarPaciente() {
        val nome = binding.etNome.text.toString().trim()
        val cpf = binding.etCpf.text.toString().trim()
        val dataNascimento = binding.etDataNascimento.text.toString().trim()
        val sexo = binding.etSexo.text.toString().trim()
        val telefone = binding.etTelefone.text.toString().trim()
        val endereco = binding.etEndereco.text.toString().trim()

        // Validação básica dos campos obrigatórios
        var valid = true

        if (nome.isEmpty()) {
            binding.etNome.error = "Obrigatório"
            valid = false
        }

        if (cpf.isEmpty()) {
            binding.etCpf.error = "Obrigatório"
            valid = false
        }

        if (dataNascimento.isEmpty()) {
            binding.etDataNascimento.error = "Obrigatório"
            valid = false
        }

        if (!valid) return


        if (!isValidCPF(cpf)) {
            Toast.makeText(this, "CPF inválido!", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidDate(dataNascimento)) {
            Toast.makeText(this, "Data de nascimento inválida ou futura!", Toast.LENGTH_SHORT).show()
            return
        }

        // Mapeia os dados do paciente para o Couchbase Lite
        val paciente = mapOf(
            "nome" to nome,
            "cpf" to cpf,
            "dataNascimento" to dataNascimento,
            "sexo" to sexo,
            "telefone" to telefone,
            "endereco" to endereco,
            "type" to "paciente"
        )

        // Salva paciente no banco e trata retorno (supondo que salvarPaciente retorna boolean)
        val sucesso = try {
            db.salvarPaciente(paciente)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

        if (sucesso) {
            Toast.makeText(this, "Paciente cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
            voltarParaMain()
        } else {
            Toast.makeText(this, "Erro ao salvar paciente. Tente novamente.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun voltarParaMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val ano = calendar.get(Calendar.YEAR)
        val mes = calendar.get(Calendar.MONTH)
        val dia = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dataFormatada = dateFormat.format(selectedDate.time)
                binding.etDataNascimento.setText(dataFormatada)
            },
            ano, mes, dia
        )
        // Bloqueia datas futuras
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun isValidCPF(cpf: String): Boolean {
        val unmaskedCpf = cpf.replace(Regex("[^\\d]"), "")
        if (unmaskedCpf.length != 11 || unmaskedCpf.all { it == unmaskedCpf[0] }) return false

        fun calcDig(cpf: String, start: Int): Int {
            var sum = 0
            for (i in 0 until start - 1) {
                sum += cpf[i].digitToInt() * (start - i)
            }
            val rest = (sum * 10) % 11
            return if (rest == 10) 0 else rest
        }

        val dig1 = calcDig(unmaskedCpf, 10)
        val dig2 = calcDig(unmaskedCpf, 11)

        return dig1 == unmaskedCpf[9].digitToInt() && dig2 == unmaskedCpf[10].digitToInt()
    }

    private fun isValidDate(data: String): Boolean {
        return try {
            val date = dateFormat.parse(data)
            date != null && !date.after(Date())
        } catch (e: Exception) {
            false
        }
    }

    // TextWatcher para aplicar máscara de CPF: 000.000.000-00
    private val cpfMaskWatcher = object : TextWatcher {
        private var isUpdating = false
        private val mask = "###.###.###-##"
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (isUpdating) return

            val str = s.toString().replace("[^\\d]".toRegex(), "")
            var formatted = ""
            var i = 0
            for (m in mask.toCharArray()) {
                if (m != '#') {
                    formatted += m
                    continue
                }
                if (i >= str.length) break
                formatted += str[i]
                i++
            }

            isUpdating = true
            binding.etCpf.setText(formatted)
            binding.etCpf.setSelection(formatted.length)
            isUpdating = false
        }
    }
}
