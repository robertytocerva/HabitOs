package com.example.api

import com.example.BuildConfig
import com.example.data.model.Habit
import com.example.data.model.UserProfile
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Gemini REST API Request & Response Data Classes ---

data class GeminiPart(
    @Json(name = "text") val text: String
)

data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>
)

data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent?
)

data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>?
)

// --- Retrofit Interface ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// --- Gemini API Client Core ---

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    private fun getApiKey(): String {
        val key = BuildConfig.GEMINI_API_KEY
        return if (key.isEmpty() || key == "MY_GEMINI_API_KEY") {
            // Fallback for debugging, user provided key or a placeholder
            "AIzaSyCpy-BdUUQ7RNggAmWlApagnbXJdSH0hZM"
        } else {
            key
        }
    }

    suspend fun generateAiResponse(prompt: String): String {
        val apiKey = getApiKey()
        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = prompt))
                )
            )
        )
        return try {
            val response = service.generateContent(apiKey, request)
            val textResult = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            textResult ?: "Diagnóstico Neural Fallido: El núcleo de la IA no emitió lecturas."
        } catch (e: Exception) {
            "Error de Conexión Cuántica: ${e.localizedMessage ?: "Consola inestable"}"
        }
    }

    // Prompts structures customized dynamically

    fun createNeuralDiagnosticPrompt(profile: UserProfile, habits: List<Habit>): String {
        val habitsJson = habits.joinToString("\n") { 
            "- [${if (it.isCompletedToday) "COMPLETADO" else "PENDIENTE"}] ${it.getCategorySpanish()}: ${it.title} (${it.description}) [Streak: ${it.streak} días]"
        }
        return """
            Actúa como un Coach de Inteligencia Artificial Avanzado de una HUD futurista cyberpunk dirigido a la superación de estudiantes universitarios.
            El estudiante posee el siguiente perfil de RPG:
            - Nombre de Usuario: ${profile.name}
            - Clase de Héroe: ${profile.characterClass} (${profile.getClassTitleSpanish()})
            - Historial de Atributos: Disciplina: ${profile.statDiscipline}/100, Concentración (Focus): ${profile.statFocus}/100, Energía: ${profile.statEnergy}/100, Mental (Mind): ${profile.statMind}/100, Cuerpo/Físico: ${profile.statBody}/100
            - Nivel Actual: ${profile.level}
            - Nivel de Estrés Inicial: ${profile.onboardingStressLevel}/10
            - Calidad del Sueño Inicial: ${profile.onboardingSleepQuality}/10
            - Horarios y Hábitos del Estudiante:
            $habitsJson

            Por favor, genera un reporte de "DIAGNÓSTICO NEURAL CENTRAL".
            Debe estar escrito en ESPAÑOL, con una vibra altamente inmersiva, tecnológica, futurista y motivadora.
            Utiliza un formato claro:
            1. 🔵 ANÁLISIS DE SISTEMAS (Comenta sobre sus hábitos actuales, fortalezas según su clase, y debilidades según sus estadísticas actuales).
            2. ⚡ PATRONES NEGATIVOS DETECTADOS (Por ejemplo, si duerme tarde, tiene estrés alto, o falta de hidratación). Se creativo e inventa un nombre técnico futurista.
            3. ⭐ CONSEJO RECOMENDADO (Un método de estudio táctico o hábito especial para balancear su vida universitaria).
            4. 🔮 TRANSMISIÓN DE MOTIVACIÓN (Un mensaje corto, épico, casi de película de ciencia ficción para inspirarlo).

            Mantén el texto conciso, limpio y con saltos de línea elegantes. No uses markdown que entorpezca la visualización de la terminal móvil.
        """.trimIndent()
    }

    fun recommendNewHabitPrompt(profile: UserProfile, category: String): String {
        return """
            Actúa como un motor holográfico de sugerencias de hábitos saludables para universitarios RPG.
            El perfil actual del estudiante:
            - Clase: ${profile.getClassTitleSpanish()}
            - Disciplina: ${profile.statDiscipline}/100, Concentración: ${profile.statFocus}/100, Energía: ${profile.statEnergy}/100, Mental: ${profile.statMind}/100, Cuerpo: ${profile.statBody}/100
            
            Sugiéreme un HÁBITO FUTURISTA súper interesante enfocado en la categoría: $category.
            Requisitos:
            - Nombre del Hábito (debe sonar tecnológico, ej: 'Protocolo de Enfoque Cero Distracción o Reto de Recarga Electrolítica')
            - Descripción del Hábito (cómo le ayuda en su vida universitaria diaria)
            - Estadística que mejora (DISCIPLINE, FOCUS, ENERGY, MIND, o BODY)
            - Dificultad recomendada (EASY, MEDIUM, o HARD)
            
            Entrégame un texto en ESPAÑOL estructurado con etiquetas fáciles, por ejemplo:
            [NOMBRE]: Nombre del hábito
            [DESCRIPCION]: Breve explicación de 2 líneas
            [ESTADISTICA]: Nombre exacto de la estadística que beneficia en mayúsculas (DISCIPLINE, FOCUS, ENERGY, MIND, o BODY)
            [DIFICULTAD]: EASY, MEDIUM o HARD
        """.trimIndent()
    }

    fun notificationPrompt(profile: UserProfile, eventDescription: String): String {
        return """
            Genera un mensaje de alerta de una sola línea corta (menos de 70 caracteres) que aparecería en la pantalla holográfica del estudiante.
            - Usuario: ${profile.name} (Clase: ${profile.getClassTitleSpanish()})
            - Suceso: $eventDescription
            
            La vibra de la notificación debe ser futurista, táctica y amigable pero imperativa. En ESPAÑOL.
            Ejemplo: "¡Núcleo de energía al límite! Bebe hidratación de inmediato." o "Estudiante, completa tus quorums de FOCUS hoy mismo."
        """.trimIndent()
    }
}
