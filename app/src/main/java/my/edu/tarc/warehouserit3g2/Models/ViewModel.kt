package my.edu.tarc.warehouserit3g2.Models


import androidx.lifecycle.ViewModel
import my.edu.tarc.warehouserit3g2.person.Person


class ViewModel: ViewModel() {
    private lateinit var aPerson : Person
    private lateinit var fullName :String
    private lateinit var partNo :String

    fun setaPerson (aPerson : Person) {
        this.aPerson = aPerson
    }

    fun setfullName (fullName :String) {
        this.fullName = fullName
    }

    fun setMin (partNo:String) {
        this.partNo = partNo
    }

    fun getfullName() :String {
        return fullName
    }

    fun getPerson(): Person {
        return aPerson
    }

    fun getMin() : String {
        return partNo
    }

    companion object{
        private var instance : my.edu.tarc.warehouserit3g2.Models.ViewModel? = null

        fun getInstance() =
            instance ?: synchronized(ViewModel::class.java){
                instance?: my.edu.tarc.warehouserit3g2.Models.ViewModel().also { instance = it }
            }
    }
}