package my.edu.tarc.warehouserit3g2.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PersonDao {

    @Insert
    fun insertPerson(p:Person )

    @Query("select * from rememberMe_table")
    fun getPerson (): Person

    @Query("delete from rememberMe_table")
    fun removeAll()
}