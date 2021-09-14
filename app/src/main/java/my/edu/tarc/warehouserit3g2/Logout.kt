package my.edu.tarc.warehouserit3g2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.person.PersonDB
import my.edu.tarc.warehouserit3g2.person.PersonDao

class Logout : Fragment() {
    private lateinit var person: ViewModel
    private lateinit var dao: PersonDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (container != null) {
            dao = PersonDB.getInstance(container.context).personDao
        }

        CoroutineScope(Dispatchers.IO).launch {
            dao.removeAll()
        }

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)




        return view
    }

}