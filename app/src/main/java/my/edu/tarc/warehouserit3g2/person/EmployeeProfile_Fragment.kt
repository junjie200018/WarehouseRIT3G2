package my.edu.tarc.warehouserit3g2.person

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Models.PersonViewModel
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentEmployeeProfileBinding
import my.edu.tarc.warehouserit3g2.databinding.FragmentStockDetailBinding
import my.edu.tarc.warehouserit3g2.stockInOut.Stock
import my.edu.tarc.warehouserit3g2.stockInOut.StockDetail_FragmentArgs

class EmployeeProfile_Fragment : DialogFragment() {
    private lateinit var binding: FragmentEmployeeProfileBinding
    private lateinit var Person: PersonViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_employee_profile_, container, false)

        val db = Firebase.firestore
        Person = PersonViewModel.getInstance()
        var email :String = ""
        var phoneNo :String = ""

        db.collection("Employees")
            .get()
            .addOnSuccessListener { result ->
                for (perInfo in result) {
                    if(perInfo.data?.get("fullName").toString() == Person.getfullName() ) {
                        binding.tvfullname.text = perInfo.data?.get("fullName").toString()
                        email = perInfo.data?.get("email").toString()
                        phoneNo = perInfo.data?.get("phoneNo").toString()
                        binding.tvemail.text = email
                        binding.tvphoneno.text = phoneNo

                    }
                }
            }

        binding.tvemail.setOnClickListener() {
            var address = email.split(",".toRegex()).toTypedArray()
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, address )
            }

            startActivity(intent)

        }

        binding.tvphoneno.setOnClickListener() {
            val telNo = Uri.parse("tel:$phoneNo")
            val intent = Intent(Intent.ACTION_DIAL, telNo)
            startActivity(intent)
        }

        return binding.root
    }
}