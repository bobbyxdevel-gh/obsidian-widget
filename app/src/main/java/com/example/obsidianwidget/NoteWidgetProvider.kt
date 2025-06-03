package com.example.obsidianwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.RemoteInput

class NoteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_ADD_NOTE) {
            val results = RemoteInput.getResultsFromIntent(intent)
            val text = results?.getCharSequence(EXTRA_NOTE_TEXT)?.toString() ?: ""
            if (text.isNotBlank()) {
                NoteRepository(context).saveNote(text)
                // Optionally show feedback
            }
        }
    }

    companion object {
        const val ACTION_ADD_NOTE = "com.example.obsidianwidget.ADD_NOTE"
        const val EXTRA_NOTE_TEXT = "extra_note_text"

        fun updateAppWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.note_widget)

            val intent = Intent(context, NoteWidgetProvider::class.java).apply {
                action = ACTION_ADD_NOTE
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, widgetId, intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                else PendingIntent.FLAG_UPDATE_CURRENT
            )
            val remoteInput = RemoteInput.Builder(EXTRA_NOTE_TEXT)
                .setLabel(context.getString(R.string.new_note))
                .build()

            // attach remote input
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                views.setOnClickResponse(R.id.addButton,
                    RemoteViews.RemoteResponse.fromPendingIntent(pendingIntent).addRemoteInput(remoteInput))
            } else {
                RemoteInput.addResultsToIntent(arrayOf(remoteInput), intent, null)
                views.setOnClickPendingIntent(R.id.addButton, pendingIntent)
            }

            manager.updateAppWidget(widgetId, views)
        }
    }
}
