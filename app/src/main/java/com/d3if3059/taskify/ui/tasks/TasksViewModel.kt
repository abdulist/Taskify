package com.d3if3059.taskify.ui.tasks

import android.app.Application
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.work.*
import com.d3if3059.taskify.data.PreferencesManager
import com.d3if3059.taskify.data.SortOrder
import com.d3if3059.taskify.data.Task
import com.d3if3059.taskify.data.TaskDao
import com.d3if3059.taskify.network.UpdateWorker
import com.d3if3059.taskify.ui.ADD_TASK_RESULT_OK
import com.d3if3059.taskify.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class TasksViewModel @ViewModelInject constructor (
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {
    val searchQuery = state.getLiveData("searchQuery","")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val taskEventChannel = Channel<TasksEvent>()
    val tasksEvent = taskEventChannel.receiveAsFlow()

    fun scheduleUpdater(app: Application) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<UpdateWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(1, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(app).enqueueUniquePeriodicWork(
            UpdateWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }

    private val tasksFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ){query, filterPreferences ->
        Pair(query,filterPreferences)
    }.flatMapLatest { (query,filterPreferences) ->
        taskDao.getTasks(query,filterPreferences.sortOrder,filterPreferences.hideCompleted)
    }
    val tasks = tasksFlow.asLiveData()


    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updatedSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted:Boolean) = viewModelScope.launch {
        preferencesManager.updatedHideCompleted(hideCompleted)
    }


    fun onTaskSelected(task:Task) = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task:Task,isChecked:Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))

    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int){
        when (result){
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        taskEventChannel.send(TasksEvent.ShowTAskSavedConfirmationMessage(text))
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
            taskEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
    }



    sealed class TasksEvent{
        object NavigateToAddTaskScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task:Task) : TasksEvent()
        data class ShowTAskSavedConfirmationMessage(val msg: String) : TasksEvent()
        object NavigateToDeleteAllCompletedScreen : TasksEvent()
    }



}



