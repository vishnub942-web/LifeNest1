package com.vishnu.lifenest.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    // ---- Task template (names) ----

    @Query("SELECT * FROM tasks ORDER BY sortOrder ASC, id ASC")
    fun getAllTasks(): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY sortOrder ASC, id ASC")
    suspend fun getAllTasksSync(): List<TaskEntity>

    @Insert
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET name = :newName WHERE id = :taskId")
    suspend fun renameTask(taskId: Long, newName: String)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    @Query("DELETE FROM task_entries WHERE taskId = :taskId")
    suspend fun deleteEntriesForTask(taskId: Long)

    // ---- Per-day entries ----

    @Query(
        """
        SELECT t.id AS taskId, t.name AS taskName, :date AS dateStr,
               COALESCE(e.status, 0) AS status,
               e.startTime AS startTime, e.endTime AS endTime,
               e.spentMinutes AS spentMinutes, e.remarks AS remarks
        FROM tasks t
        LEFT JOIN task_entries e ON e.taskId = t.id AND e.dateStr = :date
        ORDER BY t.sortOrder ASC, t.id ASC
        """
    )
    fun getTasksForDate(date: String): LiveData<List<TaskWithEntry>>

    @Query("SELECT * FROM task_entries WHERE taskId = :taskId AND dateStr = :date LIMIT 1")
    suspend fun getEntry(taskId: Long, date: String): TaskEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntry(entry: TaskEntryEntity)

    // For "3 days missed in a row" notification check and monthly completion report
    @Query("SELECT * FROM task_entries WHERE taskId = :taskId ORDER BY dateStr DESC")
    suspend fun getEntriesForTask(taskId: Long): List<TaskEntryEntity>

    @Query(
        """
        SELECT COUNT(*) FROM task_entries
        WHERE taskId = :taskId AND status = 1 AND dateStr LIKE :yearMonth || '%'
        """
    )
    suspend fun countDoneInMonth(taskId: Long, yearMonth: String): Int

    @Query("SELECT dateStr FROM task_entries WHERE taskId = :taskId AND status = 1")
    suspend fun getDoneDatesForTask(taskId: Long): List<String>
}
