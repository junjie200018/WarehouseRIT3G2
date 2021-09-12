package my.edu.tarc.warehouserit3g2.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "rememberMe_table")
data class Person(
    @PrimaryKey (autoGenerate = true)
    var id: Int,

    @ColumnInfo
    var username: String,

    @ColumnInfo
    var password: String,

    @ColumnInfo
    var role: String,

    @ColumnInfo
    var fullName: String,

    @ColumnInfo
    var email: String,

    @ColumnInfo
    var phoneNo: String,
)
