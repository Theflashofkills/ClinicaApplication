package com.thiagojunhonma.devhealthy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thiagojunhonma.devhealthy.databinding.ItemExameBinding
import com.thiagojunhonma.devhealthy.exame.Exame

class ExamesAdapter(private val lista: List<Exame>) :
    RecyclerView.Adapter<ExamesAdapter.ExameViewHolder>() {

    inner class ExameViewHolder(val binding: ItemExameBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExameViewHolder {
        val binding = ItemExameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExameViewHolder, position: Int) {
        val exame = lista[position]
        holder.binding.textNome.text = exame.nomePaciente
        holder.binding.textCpf.text = exame.cpfPaciente

        if (exame.fotoUrl != null) {
            Glide.with(holder.itemView.context)
                .load(exame.fotoUrl)
                .into(holder.binding.imageExame)
        } else {
            holder.binding.imageExame.setImageResource(R.drawable.exame) // um placeholder no drawable
        }
    }

    override fun getItemCount(): Int = lista.size
}
