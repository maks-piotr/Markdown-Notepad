package com.markdown_notepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.commit
import com.example.markdown_notepad.R
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.markdown_notepad.edit_activity_fragments.EditActivityViewModel
import com.markdown_notepad.edit_activity_fragments.ReadMarkdownFragment
import com.markdown_notepad.edit_activity_fragments.WriteMarkdownFragment

class EditActivity : AppCompatActivity() {
    private lateinit var fragmentSwitch : SwitchMaterial
    /*menu*/
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
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
        val editActivityViewModel = EditActivityViewModel()
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    /*!menu*/
    companion object {
        private const val READ_FRAGMENT = "readFragment"
        private const val WRITE_FRAGMENT = "writeFragment"
    }
}