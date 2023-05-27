package com.markdown_notepad


import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.markdown_notepad.R
import com.google.android.material.navigation.NavigationView
import com.markdown_notepad.room.Utilities
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*


class FileManagerActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var repo : Utilities ;
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result;
    }
    private suspend fun addFile(uri: Uri) {
        val inFile = applicationContext.contentResolver.openInputStream(uri);
        inFile.use{
            if(inFile!=null){
                val outFile = File(getExternalFilesDir(null), UUID.randomUUID().toString())
                inFile.copyTo(outFile.outputStream());
                //outFile.appendBytes(inFile.readNBytes(inFile.available()))
                val filename = getFileName(uri)?:"unknown";
                repo.addOneFile(filename, Uri.fromFile(outFile).toString())
                Log.i("URI File Manager", Uri.fromFile(outFile).toString());
            }
            Log.i("URI File Manager", uri.toString());
        }
    }
    private var fileChooserLauncher: ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.GetMultipleContents())
    { uris: List<@JvmSuppressWildcards Uri>? ->
        // Handle the selected file URI here
        uris?.forEach {
            runBlocking{
                addFile(uri = it);
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)
        supportActionBar?.title = getString(R.string.file_manager_activity_title)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            lateinit var intent: Intent

            when (it.itemId) {
                R.id.notesListActivity -> intent = Intent(this, NotesListActivity::class.java)
                R.id.editActivity -> intent = Intent(this, EditActivity::class.java)
                R.id.fileManagerActivity -> intent = Intent(this, FileManagerActivity::class.java)
                R.id.settingsActivity -> intent = Intent(this, SettingsActivity::class.java)
            }
            resultLauncher.launch(intent)
            true
        }
        repo = (application as MarkdownApplication).repo;
        val b: Button = findViewById(R.id.button);
        b.setOnClickListener {
            fileChooserLauncher.launch("*/*");
        };
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}