package com.example.mpaz_pro_app_movil.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import com.example.mpaz_pro_app_movil.viewmodel.ConocimientoViewModel


// TARJETA "CONECTANDO APRENDO" (API externa: Wikipedia)
// Cuatro botones, uno por asignatura. Al tocar uno se pide a Wikipedia
// el tema del día de esa asignatura y se muestra con su foto.
// No usa el backend propio ni el token de la sesión.
@Composable
fun MiradaAlMundo(viewModel: ConocimientoViewModel = viewModel()) {

    val estado by viewModel.estado.collectAsState()
    val resumen = estado.resumen
    val mensajeError = estado.error
    val urlFoto = resumen?.thumbnail?.source

    // Wikipedia exige que quien descarga una foto se identifique (User-Agent).
    val contexto = LocalPlatformContext.current
    val peticionFoto = remember(urlFoto) {
        ImageRequest.Builder(contexto)
            .data(urlFoto)
            .httpHeaders(
                NetworkHeaders.Builder()
                    .add("User-Agent", "MpazPro/1.0 (proyecto DuocUC)")
                    .build()
            )
            .build()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Conectando aprendo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Una mirada al mundo a solo un click",
                style = MaterialTheme.typography.bodyMedium
            )

            // Un botón por asignatura.
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                viewModel.temas.forEachIndexed { indice, tema ->
                    FilterChip(
                        selected = estado.temaElegido == indice,
                        onClick = { viewModel.elegir(indice) },
                        label = { Text(tema.asignatura) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    estado.cargando -> CircularProgressIndicator()

                    mensajeError != null -> Text(
                        text = mensajeError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )

                    resumen != null -> Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (urlFoto != null) {
                            // Mientras carga: circulito.
                            // Si la foto falla: no se muestra nada (sin hueco).
                            SubcomposeAsyncImage(
                                model = peticionFoto,
                                contentDescription = resumen.title,
                                modifier = Modifier.fillMaxWidth(),
                                loading = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(260.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                },
                                error = { },
                                success = {
                                    // Fit = se ve la foto COMPLETA y centrada.
                                    // Sin fondo blanco: los lados vacíos toman el
                                    // color de la tarjeta y casi no se notan.
                                    SubcomposeAsyncImageContent(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(260.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        alignment = Alignment.Center,
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            )
                        }
                        Text(
                            text = resumen.title.orEmpty(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = resumen.extract.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 6,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Text(
                text = "Fuente: Wikipedia",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}