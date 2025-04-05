package com.example.clinica

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clinica.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }

    private var clicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.addBtn.setOnClickListener {
            onAddButtonClicked()
        }

        binding.pacientBtn.setOnClickListener {
            Toast.makeText(this, "Botão PACIENTE clicado!", Toast.LENGTH_SHORT).show()
        }

        binding.examBtn.setOnClickListener {
            Toast.makeText(this, "Botão EXAME clicado!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onAddButtonClicked(){
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.examBtn.visibility = android.view.View.VISIBLE
            binding.pacientBtn.visibility = android.view.View.VISIBLE
        }else{
            binding.examBtn.visibility = android.view.View.INVISIBLE
            binding.pacientBtn.visibility = android.view.View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.examBtn.startAnimation(fromBottom)
            binding.pacientBtn.startAnimation(fromBottom)
            binding.addBtn.startAnimation(rotateOpen)
        }else{
            binding.examBtn.startAnimation(toBottom)
            binding.pacientBtn.startAnimation(toBottom)
            binding.addBtn.startAnimation(rotateClose)
        }
    }
}
