package com.abstratsystems.abstratlibrary

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.media.MediaPlayer
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*

/**
 * A helper class containing various utility functions for application management.
 */
object AbstratHelperClass {


    object App {
        private const val PERMISSION_REQUEST_CODE = 1

        /**
         * Requests necessary permissions from the user.
         *
         * @param permissions The list of permissions to request.
         * @param activity The activity in which the permissions are requested.
         */
        fun requestPermissions(permissions: ArrayList<String>, activity: Activity) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        activity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(permission),
                        PERMISSION_REQUEST_CODE
                    )
                }
            }
        }

        // Method to make all files in a directory executable
        fun makeFilesExecutable(directoryPath: String) {
            val directory = File(directoryPath)
            if (directory.exists() && directory.isDirectory) {
                val files = directory.listFiles()
                if (files != null) {
                    for (file in files) {
                        if (file.isFile) {
                            try {
                                if (file.canExecute()) {
                                    continue  // Skip if the file is already executable
                                }
                                val filePermissions = file.setExecutable(true, false)
                                if (!filePermissions) {
                                    // Handle the case where the file permission change was not successful
                                    println("Failed to make file ${file.name} executable.")
                                }
                            } catch (e: SecurityException) {
                                // Handle security exceptions if the app doesn't have the necessary permissions
                                println("Security Exception: ${e.message}")
                            } catch (e: Exception) {
                                // Handle other exceptions that might occur during the process
                                println("Exception occurred: ${e.message}")
                            }
                        }
                    }
                } else {
                    // Handle the case where the directory is empty
                    println("Directory is empty.")
                }
            } else {
                // Handle the case where the directory doesn't exist or isn't a directory
                println("Invalid directory path.")
            }
        }


    }
}



