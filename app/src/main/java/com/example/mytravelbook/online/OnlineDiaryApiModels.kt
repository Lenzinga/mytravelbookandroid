package com.example.mytravelbook.online

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single diary entry as returned by GET /diary.
 */
@Serializable
data class RemoteDiaryEntry(
    @SerialName("id")
    val id: String,                 // e.g. UUID
    @SerialName("title")
    val title: String,
    @SerialName("text")
    val text: String,
    @SerialName("images")
    val images: List<RemoteImage> = emptyList(),
    @SerialName("locationName")
    val locationName: String? = null,
    @SerialName("dateTime")
    val dateTime: String           // e.g. "2025-01-14T21:34:54.393619433Z"
)

/**
 * For the "images" array in the GET /diary response.
 */
@Serializable
data class RemoteImage(
    @SerialName("url")
    val url: String
)

/**
 * Represents the POST body for creating a new diary entry (Publish).
 */
@Serializable
data class CreateDiaryRequestBody(
    @SerialName("title")
    val title: String,
    @SerialName("text")
    val text: String,
    @SerialName("locationName")
    val locationName: String? = null,
    @SerialName("images")
    val images: List<String> = emptyList(),
    @SerialName("dateTime")
    val dateTime: String
)

/**
 * Represents the response from POST /diary, e.g. { "id": "<some-uuid>" }.
 */
@Serializable
data class DiaryCreatedResponseBody(
    val id: String
)
