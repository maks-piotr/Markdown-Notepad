package com.markdown_notepad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.androidbuts.multispinnerfilter.KeyPairBoolData
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch
import com.example.markdown_notepad.R
import com.google.android.material.navigation.NavigationView


class NotesListActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var multiSelectSpinnerWithSearch : MultiSpinnerSearch
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_list)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            lateinit var intent: Intent

            when(it.itemId) {
                R.id.notesListActivity -> intent = Intent(this, NotesListActivity::class.java)
                R.id.editActivity -> intent = Intent(this, EditActivity::class.java)
                R.id.fileManagerActivity -> intent = Intent(this, FileManagerActivity::class.java)
                R.id.settingsActivity -> intent = Intent(this, SettingsActivity::class.java)
            }
            resultLauncher.launch(intent)
            true
        }
        multiSelectSpinnerWithSearch = findViewById(R.id.multipleItemSelectionSpinner)
        multiSelectSpinnerWithSearch.setSearchHint("Select Tags")
        multiSelectSpinnerWithSearch.setEmptyTitle("Not Tags Found!")
        // If you will set the limit, this button will not display automatically.
        multiSelectSpinnerWithSearch.isShowSelectAllButton = true;

        //A text that will display in clear text button
        var tagArray = arrayListOf<KeyPairBoolData>(KeyPairBoolData("tag1",true),KeyPairBoolData("tag2",true),
            KeyPairBoolData("tag2",true),KeyPairBoolData("tag2",true),KeyPairBoolData("tag2",true),KeyPairBoolData("tag2",true),KeyPairBoolData("tag2",true),KeyPairBoolData("tag2",true),KeyPairBoolData("tag2",true))
        multiSelectSpinnerWithSearch.setClearText("Close & Clear");
        multiSelectSpinnerWithSearch.setItems(
            tagArray
        ) { items ->
            for (i in items.indices) {
                if (items[i].isSelected) {
                    Log.i("mylogs", i.toString() + " : " + items[i].name + " : " + items[i].isSelected)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}