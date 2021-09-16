package my.edu.tarc.warehouserit3g2.currectQty

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import my.edu.tarc.warehouserit3g2.ManagerActivity
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R


class CurrentQtyAdapter (private val CurrentQtyList : ArrayList<CurrentQty>, private var fm : FragmentManager,
                         private val ac : FragmentActivity
) : RecyclerView.Adapter<CurrentQtyAdapter.myViewHolder>() {

    private lateinit var person: ViewModel
    private lateinit var con: Context

    class myViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partNo: TextView = itemView.findViewById(R.id.currentqtypartno)
        val qty: TextView = itemView.findViewById(R.id.CurQty)
        val probar: ProgressBar = itemView.findViewById(R.id.progressBarCurQty)
        val per: TextView = itemView.findViewById(R.id.QtyPercentage)
        val warning : TextView = itemView.findViewById(R.id.warning)
        val delete: ImageView = itemView.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.current_qty_item, parent, false)
        con = parent.context
        return myViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        //get ViewModel
        person = ViewModel.getInstance()
        val CurrentQty = CurrentQtyList[position]
        val db = Firebase.firestore

        holder.partNo.text = CurrentQty.PartNo
        holder.qty.text = CurrentQty.Qty
        holder.probar.progress = CurrentQty.progress.toInt()
        holder.per.text = String.format ("%.2f",CurrentQty.progress) + "%"

        //Check low quantity
        if(CurrentQty.Qty == "0") {
            holder.delete.isVisible = true
        }
        if(CurrentQty.BelowMin == "yes") {
            holder.probar.progressTintList = ColorStateList.valueOf(Color.RED)
            holder.warning.text = "Low Quantity"
            holder.warning.setTextColor(Color.RED)
            holder.warning.isVisible = true

        } else if (CurrentQty.BelowMin == "null") {
            holder.probar.progressTintList = ColorStateList.valueOf(Color.rgb(210,237,252))
            holder.warning.text = "Set Minimum Quantity"
            holder.warning.setTextColor(Color.rgb(25, 152,42))
            holder.warning.isVisible = true

        }
        holder.probar.max = 100

        //interacting with recycler view item
        holder.itemView.setOnClickListener {
            val partNo = CurrentQty.PartNo
            person.setMin(partNo)
            var dialog = SetQuantityMin_Fragment()
            dialog.show(fm,"setMinQty")
        }

        holder.delete.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(con)

            //title of the dialog
            builder.setTitle("Delete the product minimum quantity notification")

            // message of the dialog
            builder.setMessage("Are you sure want to delete ? ")

            //save button of the dialog
            builder.setPositiveButton("Submit") { dialog, which ->
                db.collection("StockQuantity").document(CurrentQty.PartNo)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            con,
                            "Delete Successful.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                val intent = Intent(con, ManagerActivity::class.java)
                startActivity(con,intent, Bundle())
            }
            // cancel button of the dialog
            builder.setNegativeButton("Cancel") { dialog, which ->
            }
            builder.show()


        }

    }

    override fun getItemCount(): Int {
        return CurrentQtyList.size

    }

}