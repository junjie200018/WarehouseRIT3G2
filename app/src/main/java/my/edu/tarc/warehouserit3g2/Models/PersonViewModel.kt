package my.edu.tarc.warehouserit3g2.Models


import androidx.lifecycle.ViewModel
import my.edu.tarc.warehouserit3g2.Data.Person


class PersonViewModel: ViewModel() {
    private lateinit var aPerson : Person

    fun getUsername(): Person {
        return aPerson
    }

    fun setaPerson (aPerson : Person) {
        this.aPerson = aPerson
    }

    companion object{
        private var instance : PersonViewModel? = null

        fun getInstance() =
            instance ?: synchronized(PersonViewModel::class.java){
                instance?: PersonViewModel().also { instance = it }
            }
    }
}