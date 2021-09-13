package my.edu.tarc.warehouserit3g2.Models


import androidx.lifecycle.ViewModel
import my.edu.tarc.warehouserit3g2.person.Person


class PersonViewModel: ViewModel() {
    private lateinit var aPerson : Person
    private lateinit var fullName :String

    fun getPerson(): Person {
        return aPerson
    }

    fun setaPerson (aPerson : Person) {
        this.aPerson = aPerson
    }

    fun setfullName (fullName :String) {
        this.fullName = fullName
    }

    fun getfullName() :String {
        return fullName
    }

    companion object{
        private var instance : PersonViewModel? = null

        fun getInstance() =
            instance ?: synchronized(PersonViewModel::class.java){
                instance?: PersonViewModel().also { instance = it }
            }
    }
}