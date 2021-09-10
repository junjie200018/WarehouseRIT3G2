package my.edu.tarc.warehouserit3g2.Data

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface PersonDao {

    @Insert
    fun insertPerson(p:Person )
}