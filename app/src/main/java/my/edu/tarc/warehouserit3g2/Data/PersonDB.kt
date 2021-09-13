package my.edu.tarc.warehouserit3g2.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database (entities = [Person::class] , version = 1, exportSchema = false)
abstract class PersonDB:RoomDatabase(){
    abstract val personDao: PersonDao

    companion object {

        @Volatile
        private var INSTANCE: PersonDB? = null

        fun getInstance(context: Context): PersonDB {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PersonDB::class.java,
                        "PersonDatabase"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}