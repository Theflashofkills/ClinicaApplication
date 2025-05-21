package com.thiagojunhonma.devhealthy.ui.ChatBot

import ChatAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.thiagojunhonma.devhealthy.Modelo.Message
import com.thiagojunhonma.devhealthy.databinding.FragmentProcurarBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ChatBotFragment : Fragment() {

    private var _binding: FragmentProcurarBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ChatAdapter
    private val messages = mutableListOf<Message>()
    private val client = OkHttpClient()
    private val apiKey = "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"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProcurarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ChatAdapter(messages)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.buttonSend.setOnClickListener {
            val pergunta = binding.editTextMessage.text.toString()
            if (pergunta.isNotBlank()) {
                adicionarMensagem(pergunta, true)
                binding.editTextMessage.text.clear()
                enviarPerguntaParaOpenAI(pergunta)
            }
        }
    }

    private fun adicionarMensagem(texto: String, isUser: Boolean) {
        messages.add(Message(texto, isUser))
        adapter.notifyItemInserted(messages.size - 1)
        binding.recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun enviarPerguntaParaOpenAI(pergunta: String) {
        val client = OkHttpClient()
        val jsonBody = JSONObject().apply {
            put("model", "gpt-4o-mini")
            put("messages", JSONArray().put(JSONObject().apply {
                put("role", "user")
                put("content", pergunta)
            }))
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer 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")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    adicionarMensagem("Erro na conexão: ${e.message}", isUser = false)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                activity?.runOnUiThread {
                    try {
                        val json = JSONObject(body ?: "")
                        if (json.has("error")) {
                            val errorMsg = json.getJSONObject("error").getString("message")
                            if (errorMsg.contains("quota")) {
                                adicionarMensagem("Limite da API excedido. Por favor, aguarde ou verifique sua conta OpenAI.", isUser = false)
                            } else {
                                adicionarMensagem("Erro da API: $errorMsg", isUser = false)
                            }
                        } else {
                            val choices = json.getJSONArray("choices")
                            val resposta = choices.getJSONObject(0).getJSONObject("message").getString("content")
                            adicionarMensagem(resposta.trim(), isUser = false)
                        }
                    } catch (e: Exception) {
                        adicionarMensagem("Erro ao interpretar resposta: ${e.message}", isUser = false)
                    }
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

