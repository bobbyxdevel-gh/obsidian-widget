package com.example.obsidianwidget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<Button>(R.id.selectFolderButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, REQUEST_FOLDER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FOLDER && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                prefs.edit().putString(KEY_FOLDER_URI, uri.toString()).apply()
            }
        }
    }

    companion object {
        const val REQUEST_FOLDER = 1001
        const val PREFS = "widget_prefs"
        const val KEY_FOLDER_URI = "folder_uri"

        fun getFolderUri(context: Context): Uri? {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val str = prefs.getString(KEY_FOLDER_URI, null)
            return str?.let { Uri.parse(it) }
        }
    }
}
