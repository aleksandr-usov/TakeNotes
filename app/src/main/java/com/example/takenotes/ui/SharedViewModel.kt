package com.example.takenotes.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.takenotes.data.NoteRoomDatabase
import com.example.takenotes.data.model.api.Folder
import com.example.takenotes.data.model.api.Note
import com.example.takenotes.data.model.repo.FolderRepository
import com.example.takenotes.data.model.repo.NoteRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val _screen = MutableLiveData<Screen>()
    val screen: LiveData<Screen> = _screen

    private val _toolBarText = MutableLiveData<ToolBarText>()
    val toolBarText: LiveData<ToolBarText> = _toolBarText

    private val _sortLiveData = MutableLiveData<List<SortToChoose>>()
    val sortLiveData: LiveData<List<SortToChoose>> = _sortLiveData

    private val _currentSortLiveData = MutableLiveData<SortToChoose>()
    val currentSortLiveData: LiveData<SortToChoose> = _currentSortLiveData

    private val _allNotes = MutableLiveData<List<Note>>()

    private val _notesToDisplay = MutableLiveData<List<Note>>()
    val notesToDisplay: LiveData<List<Note>> = _notesToDisplay

    private val _selectedNotes = MutableLiveData<List<Note>>()
    val selectedNotes: LiveData<List<Note>> = _selectedNotes

    private val _currentNote = MutableLiveData<Note>()
    val currentNote: LiveData<Note> = _currentNote

    private val _currentFolder = MutableLiveData<Folder>()
    val currentFolder: LiveData<Folder> = _currentFolder

    private var _allFolders = MutableLiveData<List<Folder>>()
    val allFolders: LiveData<List<Folder>> = _allFolders

    private var deletedNotes = mutableListOf<Note>()

    private var repository: NoteRepository
    private var repositoryFolder: FolderRepository

    private val disposables = CompositeDisposable()

    private var deletionDisposable = Disposables.empty()

    init {
        _screen.value = Screen.NOTES_LIST
        //_toolBarText.value = ToolBarText.NOTES_LIST
        // _sortLiveData.value = SortToChoose.values().toList()
        _currentSortLiveData.value = SortToChoose.MODIFIED

        val noteDao = NoteRoomDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        val folderDao = NoteRoomDatabase.getDatabase(application).folderDao()
        repositoryFolder = FolderRepository(folderDao)

        disposables.add(
            repository.getAllNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { newAllNotes ->
                        _selectedNotes.value = listOf()
                        _allNotes.value = sortNotes(
                            _currentSortLiveData.value ?: SortToChoose.CREATED,
                            newAllNotes
                        )
                    },
                    {
                        it.printStackTrace()
                    })
        )

        disposables.add(
            repositoryFolder.getAllFolders()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { newAllFolders ->
                        _allFolders.value = newAllFolders
                    },
                    {
                        it.printStackTrace()
                    })
        )
        _allNotes.observeForever { _notesToDisplay.value = it }
        // _allFolders.observeForever { _allFolders.value = it }
    }

    fun onNewNoteClicked() {
        _currentNote.value = Note(
            0,
            "All",
            System.currentTimeMillis().toString(),
            System.currentTimeMillis().toString(),
            "",
            ""
        )
        _screen.value = Screen.NOTE
        _toolBarText.value = ToolBarText.NOTE
    }

    fun onNewFolderClicked() {
        _screen.value = Screen.NEW_FOLDER_DIALOG
    }

    fun insertNewFolder(name: String) {
        _currentFolder.value = Folder(
            0,
            mutableListOf(),
            System.currentTimeMillis().toString(),
            System.currentTimeMillis().toString(),
            name
        )
        disposables.add(
            repositoryFolder.insertFolder(_currentFolder.value ?: return)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {},
                    { it.printStackTrace() }
                )
        )
    }

    fun onCurrentNoteBodyChanged(newBody: String) {
        _currentNote.value?.noteText = newBody
    }

    fun saveChanges() {
        val currentNote = _currentNote.value ?: return
        val id = currentNote.id
        val folderId = "Test"
        val timeCreated = currentNote.dateCreated
        var title = currentNote.noteText.split("\n").first().trimStart().trimEnd()

        if (title.isEmpty()) {
            title = currentNote.noteText.trimEnd().substringAfterLast("\n")
        }

        val newNote = Note(
            id,
            folderId,
            timeCreated,
            System.currentTimeMillis().toString(),
            title,
            currentNote.noteText
        )
        disposables.add(
            repository.insertNote(newNote)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        newNote.id = it
                        _currentNote.value = newNote
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }

    fun deleteCurrentNote() {
        val current = currentNote.value?.id ?: return

        disposables.add(
            repository.deleteNote(current)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {

                    },
                    { it.printStackTrace() }
                )
        )
        _screen.value = Screen.NOTES_LIST
    }

    fun deleteOnSwipe(note: Note) {
        deletedNotes.add(note)

        deletionDisposable.dispose()

        deletionDisposable = Single.just(Any())
            .delay(5, TimeUnit.SECONDS)
            .flatMapCompletable {
                repository.deleteNotes(deletedNotes.map { it.id })
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    deletedNotes.clear()
                },
                { it.printStackTrace() }
            )
    }

    fun deleteMultipleNotes() {
        disposables.add(
            repository.deleteNotes(selectedNotes.value?.map { it.id } ?: listOf())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({},
                    { it.printStackTrace() }
                ))
    }

    fun undoDeleteItem() {
        _notesToDisplay.value = _notesToDisplay.value
        deletedNotes.clear()
        deletionDisposable.dispose()
    }

    fun onSortItemClicked(newlySelected: SortToChoose) {
        _screen.value = Screen.NOTES_LIST
        _currentSortLiveData.value = newlySelected
    }

    fun onNoteClicked(newlySelected: Note) {
        _currentNote.value = newlySelected
        _screen.value = Screen.NOTE
        _toolBarText.value = ToolBarText.NOTE
    }

    fun onFolderClicked(newlySelected: Folder) {
        _currentFolder.value = newlySelected
        _screen.value = Screen.NOTES_LIST
        _toolBarText.value = ToolBarText.NOTES_LIST
        _notesToDisplay.value = _allNotes.value?.filter { note ->
            val currentFolder = _currentFolder.value ?: return@filter false
            val notesInside = currentFolder.noteIds
            notesInside.contains(note.id)
        }
    }

    fun onNoteLongClicked(newlySelected: Note) {
        if (_selectedNotes.value?.contains(newlySelected) == true) {
            _selectedNotes.value = _selectedNotes.value?.minus(newlySelected)
        } else {
            _selectedNotes.value = (_selectedNotes.value ?: listOf()).plus(newlySelected)
        }
    }

    fun onBackPressed() {
        // _allFolders.value = _allNotes.value?.map { it.folderId }?.distinct()

        val newScreen =
            when (screen.value ?: return) {
                Screen.NOTE -> {
                    if (_currentNote.value?.noteText.isNullOrBlank()) {
                        deleteCurrentNote()
                    } else {
                        saveChanges()
                    }
                    _selectedNotes.value = listOf()
                    _currentNote.value = null
                    Screen.NOTES_LIST
                }

                Screen.NOTES_LIST -> Screen.FOLDERS_LIST

                else -> return
            }
        _screen.value = newScreen
    }

    fun onSortTypeClicked(sortType: SortToChoose) {
        val currentAllNotes = _allNotes.value ?: listOf()

        _currentSortLiveData.value = sortType

        _allNotes.value = sortNotes(sortType, currentAllNotes)
    }

    private fun sortNotes(sortType: SortToChoose, notes: List<Note>): List<Note> {
        return when (sortType) {
            SortToChoose.CREATED -> notes.sortedByDescending { it.dateCreated }
            SortToChoose.MODIFIED -> notes.sortedByDescending { it.dateModified }
        }
    }

    fun searchNotes(query: String) {
        if (query.isEmpty()) {
            val all = mutableListOf<Note>()
            all.addAll(_allNotes.value ?: listOf())
            _notesToDisplay.value = _allNotes.value?.toList() ?: listOf()
        } else {
            _notesToDisplay.value = _allNotes.value?.filter { it.noteText.contains(query) }
        }
    }

    fun setToolBarText() {
        val newToolBarText = when (_screen.value ?: return) {
            Screen.NOTE -> ToolBarText.NOTE
            Screen.NOTES_LIST -> ToolBarText.NOTES_LIST
            Screen.FOLDERS_LIST -> ToolBarText.FOLDERS_LIST
            Screen.NEW_FOLDER_DIALOG -> ToolBarText.FOLDERS_LIST
        }
        _toolBarText.value = newToolBarText
    }

    override fun onCleared() {
        disposables.dispose()
    }

    enum class Screen {
        NOTES_LIST, NOTE, FOLDERS_LIST, NEW_FOLDER_DIALOG
    }

    enum class ToolBarText(val displayableName: String) {
        FOLDERS_LIST("Folders"),
        NOTES_LIST("Notes List"),
        NOTE("Note")
    }

    enum class SortToChoose(val displayableName: String) {
        CREATED("Sort By: Date Created"),
        MODIFIED("Sort By: Last Modified")
    }
}
