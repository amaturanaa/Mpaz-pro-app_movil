package com.example.mpaz_pro_app_movil.red

// Informes del docente, calculados por progreso-service.
class RepositorioInformes(private val api: ApiMpaz = RedMpaz.api) {

    // Un registro por estudiante que rindió la asignación.
    suspend fun informeDeAsignacion(asignacionId: Int): List<InformeApi> =
        api.informe(asignacionId.toLong())
}