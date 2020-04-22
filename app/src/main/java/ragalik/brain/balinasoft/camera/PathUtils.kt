package ragalik.brain.balinasoft.camera

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore

class PathUtils {
    companion object {
        fun getPath(context: Context, uri: Uri): String? {
            when {
                DocumentsContract.isDocumentUri(context, uri) -> {
                    when {
                        isExternalStorageDocument(uri) -> { // ExternalStorageProvider
                            val docId = DocumentsContract.getDocumentId(uri)
                            val split = docId.split(":").toTypedArray()
                            val type = split[0]
                            val storageDefinition: String
                            return if ("primary".equals(type, ignoreCase = true)) {
                                Environment.getExternalStorageDirectory()
                                    .toString() + "/" + split[1]
                            } else {
                                storageDefinition = if (Environment.isExternalStorageRemovable()) {
                                    "EXTERNAL_STORAGE"
                                } else {
                                    "SECONDARY_STORAGE"
                                }
                                System.getenv(storageDefinition) + "/" + split[1]
                            }
                        }
                        isDownloadsDocument(uri) -> { // DownloadsProvider
                            val id = DocumentsContract.getDocumentId(uri)
                            val contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"),
                                java.lang.Long.valueOf(id)
                            )
                            return getDataColumn(context, contentUri, null, null)
                        }
                        isMediaDocument(uri) -> { // MediaProvider
                            val docId = DocumentsContract.getDocumentId(uri)
                            val split = docId.split(":").toTypedArray()
                            val type = split[0]
                            var contentUri: Uri? = null
                            when (type) {
                                "image" -> {
                                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                                }
                                "video" -> {
                                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                }
                                "audio" -> {
                                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                                }
                            }
                            val selection = "_id=?"
                            val selectionArgs = arrayOf(
                                split[1]
                            )
                            return getDataColumn(context, contentUri, selection, selectionArgs)
                        }
                    }
                }
                "content".equals(uri.scheme, ignoreCase = true) -> { // MediaStore (and general)
                    return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)
                }
                "file".equals(uri.scheme, ignoreCase = true) -> { // File
                    return uri.path
                }
            }
            return null
        }

        fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
            val column = "_data"
            val projection = arrayOf(column)
            context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
                .use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        val column_index = cursor.getColumnIndexOrThrow(column)
                        return cursor.getString(column_index)
                    }
                }
            return null
        }


        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        fun isGooglePhotosUri(uri: Uri): Boolean {
            return "com.google.android.apps.photos.content" == uri.authority
        }
    }
}