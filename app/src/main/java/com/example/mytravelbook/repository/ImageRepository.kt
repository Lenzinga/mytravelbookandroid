package com.example.mytravelbook.repository

import com.example.mytravelbook.data.Image
import com.example.mytravelbook.data.ImageDao
import kotlinx.coroutines.flow.Flow

class ImageRepository(private val imageDao: ImageDao) {

    fun getImagesForEntry(entryId: Int): Flow<List<Image>> {
        return imageDao.getImagesForEntry(entryId)
    }

    suspend fun addImage(entryId: Int, imageUri: String) {
        val newImage = Image(
            entryId = entryId,
            imageUri = imageUri
        )
        imageDao.insertImage(newImage)
    }

    suspend fun deleteImage(image: Image) {
        imageDao.deleteImage(image)
    }
}
