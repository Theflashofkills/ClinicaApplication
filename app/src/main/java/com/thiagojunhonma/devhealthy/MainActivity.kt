package com.thiagojunhonma.devhealthy

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.thiagojunhonma.devhealthy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }

    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Uso da toolbar personalizada
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_procurar, R.id.navigation_perfil)
        )

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "DEVHEALTHY"
        binding.toolbar.setTitleTextColor(resources.getColor(android.R.color.white))

        // setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        binding.addBtn.setOnClickListener {
            onAddButtonClicked()
        }

        binding.pacientBtn.setOnClickListener {
            Toast.makeText(this, "Botão Cadastrar PACIENTE clicado!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CadastroPacienteActivity::class.java)
            startActivity(intent)
        }

        binding.examBtn.setOnClickListener {
            Toast.makeText(this, "Botão Criar EXAME clicado!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        binding.examBtn.visibility = if (!clicked) android.view.View.VISIBLE else android.view.View.INVISIBLE
        binding.pacientBtn.visibility = if (!clicked) android.view.View.VISIBLE else android.view.View.INVISIBLE
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.examBtn.startAnimation(fromBottom)
            binding.pacientBtn.startAnimation(fromBottom)
            binding.addBtn.startAnimation(rotateOpen)
        } else {
            binding.examBtn.startAnimation(toBottom)
            binding.pacientBtn.startAnimation(toBottom)
            binding.addBtn.startAnimation(rotateClose)
        }
    }
}
