package com.thiagojunhonma.devhealthy.ui.perfil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PerfilViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Tela do Perfil da Conta"
    }
    val text: LiveData<String> = _text
}