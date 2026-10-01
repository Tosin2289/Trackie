package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.TrackieDao
import com.example.data.local.model.AccountEntity
import com.example.data.local.model.BillEntity
import com.example.data.local.model.CategoryEntity
import com.example.data.local.model.DebtEntity
import com.example.data.local.model.GoalEntity
import com.example.data.local.model.TransactionEntity
import com.example.data.local.model.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        GoalEntity::class,
        BillEntity::class,
        DebtEntity::class,
        UserProfileEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class TrackieDatabase : RoomDatabase() {

    abstract fun trackieDao(): TrackieDao

    companion object {
        @Volatile
        private var INSTANCE: TrackieDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TrackieDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackieDatabase::class.java,
                    "trackie_database"
                )
                .addCallback(TrackieDatabaseCallback(scope))
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance

                scope.launch(Dispatchers.IO) {
                    try {
                        if (instance.trackieDao().getCategoryCount() == 0) {
                            populateDefaultCategories(instance.trackieDao())
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("TrackieDB", "Error verifying category data", e)
                    }
                }

                instance
            }
        }
    }

    private class TrackieDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDefaultCategories(database.trackieDao())
                }
            }
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDefaultCategories(database.trackieDao())
                }
            }
        }
    }
}

/**
 * Seeds standard financial categories so users can immediately categorize expenses and income.
 * No dummy transactions, accounts, bills, or debts are added.
 */
suspend fun populateDefaultCategories(dao: TrackieDao) {
    val defaultCategories = listOf(
        CategoryEntity(1, "Food & Dining", "EXPENSE", "restaurant", "#EF4444", 0.0),
        CategoryEntity(2, "Transport & Fuel", "EXPENSE", "directions_car", "#F59E0B", 0.0),
        CategoryEntity(3, "Housing & Rent", "EXPENSE", "home", "#10B981", 0.0),
        CategoryEntity(4, "Bills & Utilities", "EXPENSE", "bolt", "#3B82F6", 0.0),
        CategoryEntity(5, "Data & Airtime", "EXPENSE", "wifi", "#06B6D4", 0.0),
        CategoryEntity(6, "Shopping & Essentials", "EXPENSE", "shopping_bag", "#8B5CF6", 0.0),
        CategoryEntity(7, "Entertainment & Media", "EXPENSE", "movie", "#F97316", 0.0),
        CategoryEntity(8, "Health & Fitness", "EXPENSE", "fitness_center", "#EC4899", 0.0),
        CategoryEntity(9, "Family Support", "EXPENSE", "family_restroom", "#E11D48", 0.0),
        CategoryEntity(10, "Education & Courses", "EXPENSE", "school", "#6366F1", 0.0),
        CategoryEntity(11, "Salary & Primary Income", "INCOME", "payments", "#10B981", 0.0),
        CategoryEntity(12, "Freelance & Business", "INCOME", "work", "#059669", 0.0),
        CategoryEntity(13, "Investments & Dividends", "INCOME", "trending_up", "#2563EB", 0.0)
    )
    dao.insertCategories(defaultCategories)
}
