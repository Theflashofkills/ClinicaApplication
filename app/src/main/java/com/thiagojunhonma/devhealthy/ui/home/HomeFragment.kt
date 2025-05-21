package com.thiagojunhonma.devhealthy.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.thiagojunhonma.devhealthy.Paciente.ListaPacientesActivity
import com.thiagojunhonma.devhealthy.databinding.FragmentHomeBinding
import com.thiagojunhonma.devhealthy.exame.ListarExamesActivity  // IMPORTAR AQUI

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnPacientes.setOnClickListener {
            val intent = Intent(requireContext(), ListaPacientesActivity::class.java)
            startActivity(intent)
        }

        binding.imageButton4.setOnClickListener {
            val intent = Intent(requireContext(), ListarExamesActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
