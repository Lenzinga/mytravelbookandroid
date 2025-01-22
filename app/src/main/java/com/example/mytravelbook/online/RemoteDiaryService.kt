package com.example.mytravelbook.online

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * A simple Ktor-based service to talk to the diaries backend.
 * Base URL: https://travel-diary.moetz.dev/api/v1
 */
object RemoteDiaryService {

    // Create a global client
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = true
                }
            )
        }
    }

    private const val BASE_URL = "https://travel-diary.moetz.dev/api/v1"

    /**
     * Fetch all diaries (GET /diary).
     */
    suspend fun fetchAllDiaries(): List<RemoteDiaryEntry> {
        val response: HttpResponse = client.get("$BASE_URL/diary")
        return response.body()
    }

    /**
     * Fetch a single diary by ID (GET /diary/{diaryId}).
     */
    suspend fun fetchDiary(diaryId: String): RemoteDiaryEntry {
        val response: HttpResponse = client.get("$BASE_URL/diary/$diaryId")
        return response.body()
    }

    /**
     * Create a new diary entry (POST /diary).
     * Returns the new ID from the server.
     *
     * NOTE: We must set 'ContentType.Application.Json' and pass the request body as JSON,
     * otherwise we get "Fail to prepare request body" errors.
     */
    suspend fun createDiary(body: CreateDiaryRequestBody): DiaryCreatedResponseBody {
        val response: HttpResponse = client.post("$BASE_URL/diary") {
            contentType(ContentType.Application.Json)    // <--- crucial
            setBody(body)                                // <--- crucial
        }
        return response.body()
    }
}
