package com.az.elib.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log

class UriToPathConverter {

    fun getPathFromUri(context: Context, uri: Uri): String? {
        Log.d("getPathFromUri", "Starting with URI: $uri")

        try {
            if (DocumentsContract.isTreeUri(uri)) {
                Log.d("getPathFromUri", "URI is a tree URI")

                // Extract the document ID from the URI
                val documentId = DocumentsContract.getTreeDocumentId(uri)
                Log.d("getPathFromUri", "Document ID: $documentId")

                // Check if the document ID starts with "primary:"
                if (documentId.startsWith("primary:")) {
                    // Extract the actual path by removing "primary:" and adding the external storage path
                    val path = documentId.replace("primary:", "")
                    val fullPath = "${Environment.getExternalStorageDirectory().path}/$path"
                    Log.d("getPathFromUri", "Final path: $fullPath")
                    return fullPath
                } else {
                    Log.e("getPathFromUri", "Document ID does not start with 'primary:'")
                }
            } else {
                Log.w("getPathFromUri", "URI is not a tree URI")
            }
        } catch (e: Exception) {
            Log.e("getPathFromUri", "Error processing URI: ${e.message}")
            e.printStackTrace()
        }

        Log.e("getPathFromUri", "Returning null")
        return null
    }
}
