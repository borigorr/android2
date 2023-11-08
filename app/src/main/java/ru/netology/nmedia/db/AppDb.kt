package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity

@Database(entities = [PostEntity::class], version = 5)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .addMigrations(Migration2_3(), Migration3_4())
                .build()
    }
}

class Migration2_3: Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE PostEntity ADD COLUMN serverId INTEGER DEFAULT 0 NOT NULL")
    }
}

class Migration3_4: Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("UPDATE PostEntity SET serverId = id")
    }
}