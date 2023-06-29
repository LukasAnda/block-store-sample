package com.example.blockstore.utils

import android.content.Context
import com.google.android.gms.auth.blockstore.Blockstore
import com.google.android.gms.auth.blockstore.DeleteBytesRequest
import com.google.android.gms.auth.blockstore.RetrieveBytesRequest
import com.google.android.gms.auth.blockstore.StoreBytesData
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BlockStoreHelper(context: Context) {
    private val client = Blockstore.getClient(context)

    var requireE2EEncryption = true

    suspend fun store(key: String, value: ByteArray) = withContext(Dispatchers.IO) {
        val request = StoreBytesData.Builder()
            .setBytes(value)
            .setKey(key)

        if (requireE2EEncryption) {
            val isEncryptionAvailable = client.isEndToEndEncryptionAvailable.await()
            request.setShouldBackupToCloud(isEncryptionAvailable)
        } else {
            request.setShouldBackupToCloud(true)
        }

        client.storeBytes(request.build()).await()
    }

    suspend fun store(vararg data: Pair<String, ByteArray>) = withContext(Dispatchers.IO) {
        data.forEach { pair ->
            store(pair.first, pair.second)
        }
    }

    suspend fun restore(key: String, defaultValue: ByteArray? = null) = withContext(Dispatchers.IO) {
        val request = RetrieveBytesRequest.Builder()
            .setKeys(listOf(key))
            .build()
        return@withContext client.retrieveBytes(request)
            .await()
            .blockstoreDataMap
            .mapValues { it.value.bytes }
            .getOrDefault(key, defaultValue)
    }

    suspend fun restoreAll() = withContext(Dispatchers.IO) {
        val request = RetrieveBytesRequest.Builder().setRetrieveAll(true).build()
        return@withContext client.retrieveBytes(request)
            .await()
            .blockstoreDataMap
            .mapValues { it.value.bytes }
    }

    suspend fun delete(vararg key: String) = withContext(Dispatchers.IO) {
        val request = DeleteBytesRequest.Builder()
            .setKeys(listOf(*key))
            .build()
        client.deleteBytes(request)
            .await()
    }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        val request = DeleteBytesRequest.Builder()
            .setDeleteAll(true)
            .build()
        client.deleteBytes(request)
            .await()
    }

    private suspend fun <T> Task<T>.await(): T = suspendCoroutine { continuation ->
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resumeWithException(
                    task.exception ?: RuntimeException("Unknown task exception")
                )
            }
        }
    }
}