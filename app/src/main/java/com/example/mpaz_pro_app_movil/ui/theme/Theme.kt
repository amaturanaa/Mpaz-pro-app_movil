
package com.example.mpaz_pro_app_movil.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

// ESQUEMA DE COLORES
private val ColoresInfantiles = lightColorScheme(

    primary = AzulPrincipal,
    onPrimary = TextoBlanco,
    primaryContainer = AzulClaro,
    onPrimaryContainer = TextoPrincipal,

    secondary = MoradoPrincipal,
    onSecondary = TextoBlanco,
    secondaryContainer = MoradoClaro,
    onSecondaryContainer = TextoPrincipal,

    tertiary = VerdePrincipal,
    onTertiary = TextoBlanco,
    tertiaryContainer = VerdeClaro,
    onTertiaryContainer = TextoPrincipal,

    background = FondoPrincipal,
    onBackground = TextoPrincipal,

    surface = FondoTarjetas,
    onSurface = TextoPrincipal,
    onSurfaceVariant = TextoSecundario,

    outline = GrisBorde
)

// FORMAS REDONDEADAS
private val FormasInfantiles = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp)
)

// TEMA GENERAL
@Composable
fun MPAZ_PRO_APP_MOVILTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColoresInfantiles,
        typography = TipografiaInfantil,
        shapes = FormasInfantiles,
        content = content
    )
}
