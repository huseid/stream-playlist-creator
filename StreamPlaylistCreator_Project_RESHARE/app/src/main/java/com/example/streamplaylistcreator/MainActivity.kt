
package com.example.streamplaylistcreator

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileWriter

class MainActivity : AppCompatActivity() {

    private val playlistsDir by lazy {
        File(filesDir, "playlists").apply { if (!exists()) mkdirs() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleSharedIntent(intent)
    }

    private fun handleSharedIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val url = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (url != null && url.startsWith("http")) {
                promptForPlaylistName(url)
            } else {
                Toast.makeText(this, "Invalid stream URL", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "No stream link shared", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun promptForPlaylistName(streamUrl: String) {
        val input = EditText(this)
        input.hint = "Enter playlist name (e.g. mytv)"

        AlertDialog.Builder(this)
            .setTitle("Save to Playlist")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    saveToPlaylist(name, streamUrl)
                } else {
                    Toast.makeText(this, "Playlist name required", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .setNegativeButton("Cancel") { _, _ -> finish() }
            .show()
    }

    private fun saveToPlaylist(name: String, url: String) {
        val file = File(playlistsDir, "$name.m3u")
        val line = "#EXTINF:-1,$name\n$url\n"

        FileWriter(file, true).use {
            if (!file.exists()) file.write("#EXTM3U\n")
            it.append(line)
        }

        Toast.makeText(this, "Stream saved to $name.m3u", Toast.LENGTH_SHORT).show()
        finish()
    }
}
