package com.markdown_notepad

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidbuts.multispinnerfilter.KeyPairBoolData
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch
import com.example.markdown_notepad.R
import com.google.android.material.navigation.NavigationView
import com.markdown_notepad.notes_activity_features.NotesListRecyclerAdapter
import com.markdown_notepad.notes_activity_features.NotesListSquare
import com.markdown_notepad.room.FileModelFactory
import com.markdown_notepad.room.FileViewModel
import com.markdown_notepad.room.entities.File
import com.markdown_notepad.room.entities.Tag


class NotesListActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var multiSelectSpinnerWithSearch : MultiSpinnerSearch
    private lateinit var recyclerView : RecyclerView
    private lateinit var editText: EditText

    var owner = this
    var tagIdMap = mutableMapOf<String,Tag>()
    var fileListTags : List<File> = listOf()
    var fileListSearch : List<File> = listOf()
    var squareList : List<NotesListSquare> = listOf(NotesListSquare("fiel1",1), NotesListSquare("fiel2",2), NotesListSquare("fiel3",3), NotesListSquare("fiel2",2))
    private var squareListener : SquareListener = SquareListener(this)
    var gridAdapter : NotesListRecyclerAdapter = NotesListRecyclerAdapter(squareList,squareListener)

    private val fileViewModel: FileViewModel by viewModels {
        FileModelFactory((application as MarkdownApplication).repo)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_list)
        supportActionBar?.setTitle("Notes Browser")


//        fileViewModel.addFile("qwerty", "path/to/file")
//        fileViewModel.addTag("tag_test1")
//        var file1 : File? = null
//        fileViewModel.files.observe(this) { files ->
//            Log.i("mylogs", "test1")
//            for (file in files) {
//                Log.i("mylogs", "test2")
//                file1 = file
//            }
//            fileViewModel.tags.observe(this) { tags ->
//                Log.i("mylogs", "tagging " + file1.toString())
//                for (tag in tags) {
//                    if (file1 != null) {
//                        Log.i("mylogs", file1!!.title + "tagged with " + tag.tagId)
//                        fileViewModel.tagFile(file1!!, tag)
//                    }
//                }
//            }
//        }
        

        editText = findViewById(R.id.editTextSearch)

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

                Log.i("mylogs", "search concluded")


                fileViewModel.filterFilesByTitle(editText.text.toString()).observe(owner) { fileList ->
                    if (fileList != null)
                        fileListSearch = fileList
                        //squareListSearch = fileList.map { NotesListSquare(it.title,it.fileId) }
                    Log.i("mylogs", fileList.toString())
                    Log.i("mylogs", "search files " +  fileListSearch.toString())
                    Log.i("mylogs", "tags files " +  fileListTags.toString())
                    gridAdapter.squareList = findCommon(fileListSearch,fileListTags).toList().map { NotesListSquare(it.title,it.fileId)}
                    gridAdapter.passGalleryState()
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewItems)
        recyclerView.layoutManager = GridLayoutManager(this,3)
        recyclerView.adapter = gridAdapter

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

        //var tagArray = arrayListOf<KeyPairBoolData>(KeyPairBoolData("tag1",true),KeyPairBoolData("tag2",true),
        //    KeyPairBoolData("tag2",true),KeyPairBoolData("tag2",true),KeyPairBoolData("tag2",true),KeyPairBoolData("tag2",true),KeyPairBoolData("tag2",true),KeyPairBoolData("tag2",true),KeyPairBoolData("tag2",true))
        var tagArray = mutableListOf<KeyPairBoolData>()
        fileViewModel.tags.observe(this) {
            for (tag in it) {
                tagArray.add(KeyPairBoolData(tag.tagName,true))
                tagIdMap[tag.tagName] = tag
            }
        }
        multiSelectSpinnerWithSearch.setClearText("Close & Clear");
        multiSelectSpinnerWithSearch.setItems(
            tagArray
        ) { items ->
            val tagListString = items.map { it.name}
            val tagListTag = mutableListOf<Tag>()
            for (tag in tagListString) {
                val tagVal = tagIdMap[tag]
                if (tagVal != null)
                    tagListTag.add(tagVal)
            }
            Log.i("mylogs", "Selected tags: $tagListString")
            Log.i("mylogs", "Selected tags: $tagListTag")
            if (tagListTag.isNotEmpty()) {
                fileViewModel.filterFilesByTags(tagListTag).observe(this) { fileList ->
                    if (fileList != null)
                        fileListTags = fileList
                    //squareListSearch = fileList.map { NotesListSquare(it.title,it.fileId) }
                    Log.i("mylogs", fileList.toString())
                    Log.i("mylogs", "search files " + fileListSearch.toString())
                    Log.i("mylogs", "tags files " + fileListTags.toString())
                    gridAdapter.squareList = findCommon(fileListSearch, fileListTags).toList()
                        .map { NotesListSquare(it.title, it.fileId) }
                    gridAdapter.passGalleryState()
                }
            }
            else {
                fileViewModel.files.observe(this) { fileList ->
                    if (fileList != null)
                        fileListTags = fileList
                    //squareListSearch = fileList.map { NotesListSquare(it.title,it.fileId) }
                    Log.i("mylogs", fileList.toString())
                    Log.i("mylogs", "search files " + fileListSearch.toString())
                    Log.i("mylogs", "tags files " + fileListTags.toString())
                    gridAdapter.squareList = findCommon(fileListSearch, fileListTags).toList()
                        .map { NotesListSquare(it.title, it.fileId) }
                    gridAdapter.passGalleryState()
                }
            }
        }


        fileViewModel.files.observe(this) { fileList ->
            if (fileList != null) {
                squareList = fileList.map { NotesListSquare(it.title, it.fileId) }
                fileListTags = fileList
                fileListSearch = fileList
            }
            Log.i("mylogs", fileList.toString())
            Log.i("mylogs", squareList.toString())
            gridAdapter.squareList = squareList
            gridAdapter.passGalleryState()
        }
        Log.i("mylogs2","test")



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class SquareListener(NA : NotesListActivity) {
        private val notesActivity : NotesListActivity = NA
        fun squareClick(square: NotesListSquare) {
            notesActivity.squareClick(square)
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                gridAdapter.passGalleryState()
            }
        }


        Log.i("mylogs", "test3 " + result.resultCode)
    }

    private fun squareClick(square: NotesListSquare) {
        val focusIntent = Intent(this, EditActivity::class.java)
        focusIntent.putExtra("id", square.note_id)
        Log.i("mylogs", "Enter EditActivity")
        startForResult.launch(focusIntent)
    }
    fun <T> findCommon(first: List<T>, second: List<T>): Set<T> {
        return first.filter(second::contains).toSet()
    }
}