package com.markdown_notepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.commit
import com.example.markdown_notepad.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.markdown_notepad.edit_activity_fragments.*

class EditActivity : AppCompatActivity() {
    private lateinit var fragmentSwitch : SwitchMaterial
    private lateinit var noteTitleTextView: TextView
    private lateinit var saveNoteButton: MaterialButton
    private lateinit var openPopupMenuButton : MaterialButton
    private lateinit var popupMenu: PopupMenu
    private val editActivityViewModel : EditActivityViewModel by viewModels {
        EditActivityViewModelFactory((application as MarkdownApplication).repo)
    }
    /*menu*/
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    /* !menu */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        supportActionBar?.title = "Edit Note"
        /* menu */
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
        /*! menu*/
        val id : Int? = intent.extras?.getInt(INTENT_FILE_ID)
        if (id == null) {
            editActivityViewModel.initializeNewNote("Note")
        } else {
            editActivityViewModel.loadFile(id)
        }
        Log.i("start edit", id.toString())

        fragmentSwitch = findViewById(R.id.toggleEditModeSwitch)
        fragmentSwitch.setOnCheckedChangeListener { _, isChecked ->
            editActivityViewModel.switchDisplayMode(!isChecked)
        }
        editActivityViewModel.isReadMode.observe(this) {
            if (it) {
                supportFragmentManager.commit {
                    replace(R.id.fragmentContainer, ReadMarkdownFragment(), READ_FRAGMENT)
                    setReorderingAllowed(true)
                }
            } else {
                supportFragmentManager.commit {
                    replace(R.id.fragmentContainer, WriteMarkdownFragment(), WRITE_FRAGMENT)
                    setReorderingAllowed(true)
                }
            }
        }
        noteTitleTextView = findViewById(R.id.noteTitleTextView)
        noteTitleTextView.setOnClickListener {
            showEditNoteDetailsFragment()
        }
        if (id == null) {
            showEditNoteDetailsFragment()
        }
        editActivityViewModel.noteTitle.observe(this) {
            noteTitleTextView.text = it
        }

        saveNoteButton = findViewById(R.id.saveNoteButton)
        saveNoteButton.setOnClickListener {
            editActivityViewModel.saveFile(application)
        }
        openPopupMenuButton = findViewById(R.id.popupMenuButton)
        popupMenu = PopupMenu(this, openPopupMenuButton)
        popupMenu.menuInflater.inflate(R.menu.edit_activity_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.editActivityMenuDeleteNote -> {
                    editActivityViewModel.deleteNote()
                    finish()
                }
            }
            false
        }
        openPopupMenuButton.setOnClickListener {
            popupMenu.show()
        }
    }
    /* menu */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    /*!menu*/

    private fun showEditNoteDetailsFragment() {
        EditNoteDetailsFragment().show(supportFragmentManager, EDIT_DETAILS_FRAGMENT)
    }

    companion object {
        private const val READ_FRAGMENT = "readFragment"
        private const val WRITE_FRAGMENT = "writeFragment"
        private const val EDIT_DETAILS_FRAGMENT = "editDetailsFragment"
        private const val INTENT_FILE_ID = "id"
    }
}