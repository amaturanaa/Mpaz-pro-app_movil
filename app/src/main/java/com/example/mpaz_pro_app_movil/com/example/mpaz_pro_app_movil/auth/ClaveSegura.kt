package com.example.mpaz_pro_app_movil.auth

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

// =============================================
// CIFRADO DE CONTRASEÑAS
// =============================================
// Las contraseñas NUNCA se guardan tal como se escriben.
// Se guarda solo una versión cifrada que no se puede revertir (un "hash"):
//   - sal   : texto aleatorio distinto para cada usuario. Así, dos personas con
//             la misma contraseña quedan con hashes distintos.
//   - PBKDF2: método estándar para cifrar contraseñas. Repite el cálculo
//             muchas veces para que adivinar una clave sea lento.
// Para entrar, se cifra lo que la persona escribe con la misma sal y se
// compara con lo guardado.
object ClaveSegura {

    // PBKDF2WithHmacSHA256 solo existe desde Android 8 (API 26) y esta app
    // llega hasta Android 7 (API 24), por eso se usa la variante con SHA1.
    private const val ALGORITMO = "PBKDF2WithHmacSHA1"
    private const val ITERACIONES = 10_000
    private const val LARGO_HASH_EN_BITS = 256
    private const val LARGO_SAL_EN_BYTES = 16

    // Crea una sal aleatoria, escrita como texto.
    fun generarSal(): String {
        val sal = ByteArray(LARGO_SAL_EN_BYTES)
        SecureRandom().nextBytes(sal)
        return Base64.encodeToString(sal, Base64.NO_WRAP)
    }

    // Convierte la contraseña en su versión cifrada, usando la sal.
    fun cifrar(clave: String, sal: String): String {
        val especificacion = PBEKeySpec(
            clave.toCharArray(),
            Base64.decode(sal, Base64.NO_WRAP),
            ITERACIONES,
            LARGO_HASH_EN_BITS
        )
        try {
            val bytes = SecretKeyFactory.getInstance(ALGORITMO)
                .generateSecret(especificacion)
                .encoded
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        } finally {
            // Borra la contraseña de la memoria apenas se termina de usar.
            especificacion.clearPassword()
        }
    }

    // true si la contraseña escrita corresponde a la cifrada que está guardada.
    fun coincide(clave: String, sal: String, claveCifrada: String): Boolean {
        val calculada = cifrar(clave, sal)
        // isEqual compara siempre completo, sin pistas por el tiempo que demora.
        return MessageDigest.isEqual(
            calculada.toByteArray(Charsets.UTF_8),
            claveCifrada.toByteArray(Charsets.UTF_8)
        )
    }
}