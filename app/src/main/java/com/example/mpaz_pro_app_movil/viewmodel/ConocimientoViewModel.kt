package com.example.mpaz_pro_app_movil.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpaz_pro_app_movil.red.RedWikipedia
import com.example.mpaz_pro_app_movil.red.RepositorioWikipedia
import com.example.mpaz_pro_app_movil.red.ResumenWiki
import com.example.mpaz_pro_app_movil.red.TemasWiki
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Todo lo que la tarjeta "Conectando aprendo" necesita para dibujarse.
data class EstadoConocimiento(
    val temaElegido: Int = 0,
    val resumen: ResumenWiki? = null,
    val cargando: Boolean = false,
    val error: String? = null
)

class ConocimientoViewModel(
    private val repositorio: RepositorioWikipedia = RepositorioWikipedia(RedWikipedia.api)
) : ViewModel() {

    // Los 4 botones (uno por asignatura).
    val temas = TemasWiki.lista

    private val _estado = MutableStateFlow(EstadoConocimiento())
    val estado: StateFlow<EstadoConocimiento> = _estado.asStateFlow()

    init {
        // Al abrir la pantalla se carga el tema de hoy de la primera asignatura.
        elegir(0)
    }

    // Se llama al tocar un botón: pide a Wikipedia el tema de hoy de esa asignatura.
    fun elegir(indice: Int) {
        val tema = temas.getOrNull(indice) ?: return

        _estado.update {
            it.copy(temaElegido = indice, cargando = true, error = null)
        }

        viewModelScope.launch {
            repositorio.resumenDelDia(tema)
                .onSuccess { resumen ->
                    _estado.update { it.copy(resumen = resumen, cargando = false) }
                }
                .onFailure {
                    _estado.update {
                        it.copy(
                            cargando = false,
                            error = "No se pudo cargar el resumen. Revisa tu conexión a internet."
                        )
                    }
                }
        }
    }
}