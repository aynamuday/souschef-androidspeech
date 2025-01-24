package com.samsantech.souschef.data

import android.content.Context
import java.io.File

class InstructionsAudioLocalDataSource(private val context: Context) {

    private val dirName = "recipe_instructions_audio"

    fun getInstructionsAudioDir(): File {
        val dir = File(context.cacheDir, dirName)

        if (!dir.exists()) {
            dir.mkdir()
        }

        return dir
    }

    fun getInstructionAudioFile(fileName: String): File? {
        val dir = getInstructionsAudioDir()
        val file = File(dir, fileName)

        return if(file.exists()) {
            file
        } else {
            null
        }
    }

    fun clearInstructionsAudioDirectory() {
        val dir = getInstructionsAudioDir()

        if(dir.exists()) {
            dir.listFiles()?.forEach { file ->
                file.delete()
            }
        }
    }
}