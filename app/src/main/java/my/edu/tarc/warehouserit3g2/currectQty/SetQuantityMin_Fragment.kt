package my.edu.tarc.warehouserit3g2.currectQty

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.databinding.FragmentSetQuantityMinBinding
import my.edu.tarc.warehouserit3g2.ManagerActivity


class SetQuantityMin_Fragment : DialogFragment() {
    private lateinit var binding: FragmentSetQuantityMinBinding
    private lateinit var person: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_quantity_min_, container, false)

        //get View Model

        //connect firebase
        person = ViewModel.getInstance()
        val db = Firebase.firestore

        db.collection("StockQuantity").document(person.getMin())
            .get()
            .addOnSuccessListener { result ->

                binding.tvpartno2.text = result.data?.get("PartNo").toString()
                binding.setmin.setText(result.data?.get("MinQty").toString())
            }

        //set minimum quantity
        binding.btnsubmit.setOnClickListener() {

            //check empty
            if(binding.setmin.text.toString().isEmpty() || binding.setmin.text.toString().isBlank()) {
                binding.setminLayout.error = "Cannot be empty !"
                binding.setmin.requestFocus()

            } else if(binding.setmin.text.toString().toInt() == 0) {
                binding.setminLayout.error = "Min Quantity cannot be zero !"
                binding.setmin.requestFocus()

            }else {
                binding.setminLayout.isErrorEnabled = false

                //update to database
                db.collection("StockQuantity").document(person.getMin())
                    .update(
                        mapOf(
                            "MinQty" to binding.setmin.text.toString().toInt()
                        )
                    )

                Toast.makeText(context, "Set Successful", Toast.LENGTH_LONG).show()

                //close dialog
                val prev = parentFragmentManager.findFragmentByTag("setMinQty") as DialogFragment
                prev.dismiss()

                //reload page
                val intent = Intent(activity, ManagerActivity::class.java)
                startActivity(intent)

            }
        }

        return binding.root
    }

}