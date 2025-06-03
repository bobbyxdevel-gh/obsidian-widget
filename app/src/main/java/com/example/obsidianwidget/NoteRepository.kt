package com.example.obsidianwidget

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteRepository(private val context: Context) {

    fun saveNote(text: String) {
        val folderUri: Uri = SettingsActivity.getFolderUri(context) ?: return
        val folder = DocumentFile.fromTreeUri(context, folderUri) ?: return
        val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".md"
        val note = folder.createFile("text/markdown", name) ?: return
        context.contentResolver.openOutputStream(note.uri)?.use { out ->
            out.write(text.toByteArray())
        }
    }
}
