package com.example.takenotes.ui

import android.app.Application
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.takenotes.R
import com.example.takenotes.ui.SharedViewModel.ToolBarText.FOLDERS_LIST
import com.example.takenotes.ui.fragments.FoldersFragment
import com.example.takenotes.ui.fragments.NewFolderNameDialogFragment
import com.example.takenotes.ui.fragments.NoteFragment
import com.example.takenotes.ui.fragments.NoteListFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val factory by lazy {
        SharedViewModelFactory(applicationContext as Application)
    }

    private val viewModel by lazy {
        return@lazy ViewModelProvider(this, factory).get(SharedViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = tb_top_toolbar
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.parseColor("#7a3400"))

        initViews()
        initLiveData()

    }

    private fun initViews() {
        tb_top_toolbar.setNavigationOnClickListener {
            viewModel.onBackPressed()
        }
    }

    private fun initLiveData() {
        viewModel.toolBarText.observe(this, Observer {
            tb_top_toolbar.title = it?.displayableName

            when (it ?: return@Observer) {
                FOLDERS_LIST -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
        })

        viewModel.screen.observe(this, Observer {
            val fragment = when (it ?: return@Observer) {
                SharedViewModel.Screen.NOTES_LIST -> NoteListFragment()
                SharedViewModel.Screen.NOTE -> NoteFragment()
                SharedViewModel.Screen.FOLDERS_LIST -> FoldersFragment()
                SharedViewModel.Screen.NEW_FOLDER_DIALOG -> {
                    NewFolderNameDialogFragment().show(supportFragmentManager, null)
                    return@Observer
                }
            }

            supportFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fl_container, fragment)
                .commit()
        })
    }
}
