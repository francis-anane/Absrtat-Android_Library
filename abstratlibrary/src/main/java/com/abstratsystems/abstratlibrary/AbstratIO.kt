package com.abstratsystems.abstratlibrary

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * A helper class containing various utility functions for input/output operations.
 */

object AbstratIO {

    object FileIO{
        private const val TAG = "AbstratHelperClass"
        object PrivateStorageIO {
            /**
             * Creates directories within the internal storage of the application.
             *
             * @param context The context of the application.
             * @param directoryName The name of the directory to be created.
             * @return The absolute path of the created directory.
             */
            fun createDirectories(context: Context, directoryName: String): String {
                val targetDirectory = File(context.filesDir, directoryName)

                if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
                    Log.e(TAG, "Failed to create directory: $directoryName")
                }
                return targetDirectory.absolutePath
            }

            /**
             * Creates a directory within the internal storage of the application.
             *
             * @param context The context of the application.
             * @param directoryName The name of the directory to be created.
             * @return The absolute path of the created directory.
             */
            fun createDirectory(context: Context, directoryName: String): String {
                val targetDirectory = File(context.filesDir, directoryName)

                if (!targetDirectory.exists() && !targetDirectory.mkdir()) {
                    Log.e(TAG, "Failed to create directory: $directoryName")
                }
                return targetDirectory.absolutePath
            }
            /**
             * Copies asset files from the assets directory to the specified target directory.
             *
             * @param context The context of the application.
             * @param targetDirectory The directory to which the asset files are to be copied.
             * @param assetFilesDirectory The directory within assets from which to copy the files.
             */
            fun copyAssetFiles(
                context: Context,
                targetDirectory: File,
                assetFilesDirectory: String
            ) {
                try {
                    val assetManager = context.assets
                    val fileList = assetManager.list(assetFilesDirectory)

                    if (fileList != null) {
                        for (filename in fileList) {
                            val inputStream: InputStream =
                                assetManager.open("$assetFilesDirectory/$filename")
                            val bufferedInputStream = BufferedInputStream(inputStream)
                            val outFile = File(targetDirectory, filename)
                            val bufferedOutputStream = BufferedOutputStream(outFile.outputStream())

                            val totalSize = bufferedInputStream.available()
                            var copiedSize = 0

                            val buffer = ByteArray(1024)
                            var read: Int

                            while (bufferedInputStream.read(buffer).also { read = it } != -1) {
                                bufferedOutputStream.write(buffer, 0, read)
                                copiedSize += read
                                // calculate progress as a percentage
                                val progress = (copiedSize * 100) / totalSize
                                Log.d(TAG, "Copying progress: $progress%")
                            }

                            bufferedInputStream.close()
                            bufferedOutputStream.close()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error copying asset files: ${e.message}")
                }
            }
            /**
             * Deletes the specified asset file from the app's assets.
             *
             * @param context The context of the application.
             * @param fileName The name of the asset file to delete.
             * @return True if the file is successfully deleted; false if the deletion fails.
             */
            fun deleteAssetFile(context: Context, fileName: String): Boolean {
                return try {
                    val assetFilePath = "${context.filesDir}/$fileName"
                    val fileToDelete = File(assetFilePath)
                    fileToDelete.exists() && fileToDelete.delete()
                } catch (e: Exception) {
                    Log.e(TAG, "Error: Failed to delete asset file: ${e.message}")
                    false // Failed to delete the file
                }
            }

            /**
             * Deletes the specified asset directory and its contents from the app's assets.
             *
             * @param context The context of the application.
             * @param directoryName The name of the asset directory to delete.
             * @return True if the directory and its contents are successfully deleted; false if the deletion fails.
             */
            fun deleteAssetDirectory(context: Context, directoryName: String): Boolean {
                try {
                    val assetManager = context.assets
                    val fileList = assetManager.list(directoryName)

                    if (fileList != null) {
                        for (filename in fileList) {
                            val assetFilePath = "$directoryName/$filename"
                            val fileToDelete = File("${context.filesDir}/$assetFilePath")
                            if (fileToDelete.exists()) {
                                if (fileToDelete.isDirectory) {
                                    // Recursively delete subdirectories and files
                                    deleteAssetDirectory(context, assetFilePath)
                                } else {
                                    if (!fileToDelete.delete()) {
                                        return false // Failed to delete the file
                                    }
                                }
                            }
                        }
                    }
                    return true
                } catch (e: Exception) {
                    Log.e(TAG, "Error: Failed to delete asset directory: ${e.message}")
                    return false // Failed to delete the directory
                }
            }


            /**
             * Retrieves the absolute path of the specified directory within the internal storage of the application.
             *
             * @param context The context of the application.
             * @param directoryName The name of the directory to retrieve the path for.
             * @return The absolute path of the specified directory, if it exists; otherwise, an empty string.
             */
            fun getDirectoryPath(context: Context, directoryName: String): String {
                val targetDirectory = File(context.filesDir, directoryName)

                if (targetDirectory.exists()) {
                    return targetDirectory.absolutePath
                }
                return ""
            }

            /**
             * Retrieves the absolute path of the specified file within the internal storage of the application.
             *
             * @param context The context of the application.
             * @param fileName The name of the file to retrieve the path for.
             * @return The absolute path of the specified file, if it exists; otherwise, an empty string.
             */
            fun getFilePath(context: Context, fileName: String): String {
                val targetFile = File(context.filesDir, fileName)

                if (targetFile.exists()) {
                    return targetFile.absolutePath
                }
                return ""
            }

            /**
             * Retrieves the internal storage of the application.
             *
             * @param context The context of the application.
             * @return The internal storage path of the application
             */
            fun getAppStoragePath(context: Context): String {
                return context.filesDir.absolutePath
            }

            /**
             * Creates a text file in the internal storage of the application.
             *
             * @param context The context of the application.
             * @param fileName The name of the text file to create.
             * @param fileContent The content to write to the text file.
             * @return True if the file is successfully created; false if the creation fails.
             */
            fun createTextFile(context: Context, fileName: String, fileContent: String): Boolean {
                return try {
                    val file = File(context.filesDir, fileName)
                    file.writeText(fileContent)
                    true // File created successfully
                } catch (e: Exception) {
                    Log.e(TAG, "Error: Failed to text file: ${e.message}")
                    false // Failed to create the file
                }
            }

            /**
             * Creates an image file in the internal storage of the application.
             *
             * @param context The context of the application.
             * @param fileName The name of the image file to create.
             * @param bitmap The Bitmap image to save.
             * @return The absolute path of the created image file if successful, an empty string if the creation fails.
             */
            fun createImageFile(context: Context, fileName: String, bitmap: Bitmap): String {
                return try {
                    val file = File(context.filesDir, fileName)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()

                    val fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(byteArray)
                    fileOutputStream.flush()
                    fileOutputStream.close()

                    file.absolutePath // Return the absolute path of the created image file
                } catch (e: Exception) {
                    Log.e(TAG, "Error: Failed to image file: ${e.message}")
                    e.printStackTrace()
                    "" // Return an empty string if the creation fails
                }
            }


            /**
             * Creates a video file in the internal storage of the application.
             *
             * @param context The context of the application.
             * @param fileName The name of the video file to create.
             * @return The absolute path of the created video file, or an empty string if creation fails.
             */
            @RequiresApi(Build.VERSION_CODES.Q)
            fun createVideoFile(context: Context, fileName: String): String {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Video.Media.TITLE, fileName)
                    put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                    put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
                }

                val resolver = context.contentResolver
                val videoCollection =
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_INTERNAL)
                val videoFileUri = resolver.insert(videoCollection, contentValues)

                try {
                    videoFileUri?.let { uri ->
                        val outputStream: OutputStream? = resolver.openOutputStream(uri)
                        outputStream.use { output ->
                            // Write the data into the stream
                            output?.write(byteArrayOf(0x00))
                        }
                    }
                } catch (e: IOException) {
                    Log.e("createVideoFile", "IOException: ${e.message}")
                    return "" // Return an empty string if creation fails
                }

                return videoFileUri?.path
                    ?: "" // Return the path of the created video file, or an empty string if creation fails
            }

        }

        object ExternalStorageIO{
            /**
             * Creates directories within the external storage of the application.
             *
             * @param context The context of the application.
             * @param directoryName The name of the directory to be created.
             * @return The absolute path of the created directory.
             */

            fun createDirectories(context: Context, directoryName: String): String {
                val targetDirectory = File(context.getExternalFilesDir(null), directoryName)

                if (!targetDirectory.exists() && !targetDirectory.mkdirs()) {
                    Log.e(TAG, "Failed to create directory: $directoryName")
                }
                return targetDirectory.absolutePath
            }

            /**
             * Creates a directory within the internal storage of the application.
             *
             * @param context The context of the application.
             * @param directoryName The name of the directory to be created.
             * @return The absolute path of the created directory.
             */
            fun createDirectory(context: Context, directoryName: String): String {
                val targetDirectory = File(context.getExternalFilesDir(null), directoryName)

                if (!targetDirectory.exists() && !targetDirectory.mkdir()) {
                    Log.e(TAG, "Failed to create directory: $directoryName")
                }
                return targetDirectory.absolutePath
            }

            /**
             * Retrieves the absolute path of the specified directory within the External storage.
             *
             * @param context The context of the application.
             * @param directoryName The name of the directory to retrieve the path for.
             * @return The absolute path of the specified directory, if it exists; otherwise, an empty string.
             */
            fun getDirectoryPath(context: Context, directoryName: String): String {
                val targetDirectory = File(context.getExternalFilesDir(null), directoryName)

                if (targetDirectory.exists()) {
                    return targetDirectory.absolutePath
                }
                return ""
            }

            /**
             * Retrieves the absolute path of the specified file within the external storage.
             *
             * @param context The context of the application.
             * @param fileName The name of the file to retrieve the path for.
             * @return The absolute path of the specified file, if it exists; otherwise, an empty string.
             */
            fun getFilePath(context: Context, fileName: String): String {
                val targetFile = File(context.getExternalFilesDir(null), fileName)

                if (targetFile.exists()) {
                    return targetFile.absolutePath
                }
                return ""
            }

            /**
             * Retrieves the External storage of path.
             *
             * @param context The context of the application.
             * @return The external storage path of the application
             */
            fun getExternalStoragePath(context: Context): String {
                return context.getExternalFilesDir(null).toString()
            }

            /**
             * Creates a text file in the external storage of the application.
             *
             * @param context The context of the application.
             * @param fileName The name of the text file to create.
             * @param fileContent The content to write to the text file.
             * @return True if the file is successfully created; false if the creation fails.
             */
            fun createTextFile(context: Context, fileName: String, fileContent: String): Boolean {
                return try {
                    val file = File(context.getExternalFilesDir(null), fileName)
                    file.writeText(fileContent)
                    true // File created successfully
                } catch (e: Exception) {
                    Log.e(TAG, "Error: Failed to text file: ${e.message}")
                    false // Failed to create the file
                }
            }

            /**
             * Creates an image file in the external storage of the application.
             *
             * @param context The context of the application.
             * @param fileName The name of the image file to create.
             * @param bitmap The Bitmap image to save.
             * @return The absolute path of the created image file if successful, an empty string if the creation fails.
             */
            fun createImageFile(context: Context, fileName: String, bitmap: Bitmap): String {
                return try {
                    val file = File(context.getExternalFilesDir(null), fileName)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()

                    val fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(byteArray)
                    fileOutputStream.flush()
                    fileOutputStream.close()

                    file.absolutePath // Return the absolute path of the created image file
                } catch (e: Exception) {
                    Log.e(TAG, "Error: Failed to image file: ${e.message}")
                    e.printStackTrace()
                    "" // Return an empty string if the creation fails
                }
            }


            /**
             * Creates a video file in the external storage of the application.
             *
             * @param context The context of the application.
             * @param fileName The name of the video file to create.
             * @return The absolute path of the created video file, or an empty string if creation fails.
             */
            @RequiresApi(Build.VERSION_CODES.Q)
            fun createVideoFile(context: Context, fileName: String): String {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Video.Media.TITLE, fileName)
                    put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                    put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
                }

                val resolver = context.contentResolver
                val videoCollection =
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                val videoFileUri = resolver.insert(videoCollection, contentValues)

                try {
                    videoFileUri?.let { uri ->
                        val outputStream: OutputStream? = resolver.openOutputStream(uri)
                        outputStream.use { output ->
                            // Write the data into the stream
                            output?.write(byteArrayOf(0x00))
                        }
                    }
                } catch (e: IOException) {
                    Log.e("createVideoFile", "IOException: ${e.message}")
                    return "" // Return an empty string if creation fails
                }

                return videoFileUri?.path
                    ?: "" // Return the path of the created video file, or an empty string if creation fails
            }

        }


        object  ReadMedia{

            /**
             * Reads a text file and returns the content as a string.
             *
             * @param filePath The path of the text file.
             * @return The content of the text file as a string, or an empty string if the file is not found or an error occurs.
             */
            fun text(filePath: String): String {
                return try {
                    val file = File(filePath)
                    val text = StringBuilder()
                    val bufferedReader = BufferedReader(file.reader())
                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        text.append(line)
                        text.append('\n')
                    }
                    bufferedReader.close()
                    text.toString()
                } catch (e: Exception) {
                    Log.e(TAG, "Error: Failed to read text file: ${e.message}")
                    ""
                }
            }

            /**
             * Reads an image file and returns a Bitmap.
             *
             * @param filePath The path of the image file.
             * @return The Bitmap of the image, or null if the file is not found or an error occurs.
             */
            fun image(filePath: String): Bitmap? {
                return try {
                    val file = File(filePath)
                    BitmapFactory.decodeFile(file.absolutePath)
                } catch (e: Exception) {
                    Log.e(TAG, "Error: Failed to read image file: ${e.message}")
                    null
                }
            }
            /**
             * Reads a video file and plays it.
             *
             * @param filePath The path of the video file.
             */
            fun playVideo(filePath: String) {
                try {
                    val mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(filePath)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                } catch (e: Exception) {
                    Log.e(TAG, "Error: Failed to read video file: ${e.message}")
                }
            }
        }


//        /**
//         * Reads a PDF file and returns the text content.
//         *
//         * @param filePath The path of the PDF file.
//         * @return The text content of the PDF, or an empty string if the file is not found or an error occurs.
//         */
//        fun pdf(filePath: String): String {
//            return try {
//                val file = File(filePath)
//                val document = PDDocument.load(file)
//                val pdfStripper = PDFTextStripper()
//                val text = pdfStripper.getText(document)
//                document.close()
//                text
//            } catch (e: Exception) {
//                Log.e(TAG, "Error: Failed to read PDF file: ${e.message}")
//                ""
//            }
//        }


        //    object CreateDocument{
//        /**
//         * Creates a PDF document with a sample content.
//         *
//         * @param filePath The file path where the PDF document will be created.
//         */
//        fun pdf(filePath: String) {
//            val pdfDocument = PdfDocument()
//            val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
//            val page = pdfDocument.startPage(pageInfo)
//            val canvas = page.canvas
//
//            val paint = Paint()
//            paint.color = Color.BLUE
//            canvas.drawPaint(paint)
//
//            paint.color = Color.WHITE
//            canvas.drawCircle(150f, 150f, 100f, paint)
//
//            val title = "Sample PDF Document"
//            paint.color = Color.BLACK
//            paint.textSize = 12f
//            canvas.drawText(title, 80f, 300f, paint)
//
//            pdfDocument.finishPage(page)
//
//            try {
//                val file = File(filePath)
//                pdfDocument.writeTo(FileOutputStream(file))
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            pdfDocument.close()
//        }
//    }

    }
}