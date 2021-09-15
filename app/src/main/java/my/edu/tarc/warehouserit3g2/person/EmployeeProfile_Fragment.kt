package my.edu.tarc.warehouserit3g2.person

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentEmployeeProfileBinding

class EmployeeProfile_Fragment : DialogFragment() {
    private lateinit var binding: FragmentEmployeeProfileBinding
    private lateinit var person: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_employee_profile_, container, false)

        //connect firebase
        val db = Firebase.firestore

        //get view model
        person = ViewModel.getInstance()

        var email = ""
        var phoneNo = ""

        db.collection("Employees")
            .get()
            .addOnSuccessListener { result ->
                for(perInfo in result)
                {
                    //display worker personal detail
                    if(perInfo.data?.get("fullName").toString() == person.getfullName()) {
                        binding.tvfullname.text = perInfo.data?.get("fullName").toString()
                        email = perInfo.data?.get("email").toString()
                        phoneNo = perInfo.data?.get("phoneNo").toString()
                        binding.tvemail.text = email
                        binding.tvphoneno.text = phoneNo
                    }
                }
            }
            .addOnFailureListener {
                Log.d("fail", "Fail to load data")
            }

        //intent send email
        binding.tvemail.setOnClickListener() {
            var address = email.split(",".toRegex()).toTypedArray()
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, address )
            }

            startActivity(intent)

        }

        //intent calling
        binding.tvphoneno.setOnClickListener() {
            val telNo = Uri.parse("tel:$phoneNo")
            val intent = Intent(Intent.ACTION_DIAL, telNo)
            startActivity(intent)
        }

        return binding.root
    }

}