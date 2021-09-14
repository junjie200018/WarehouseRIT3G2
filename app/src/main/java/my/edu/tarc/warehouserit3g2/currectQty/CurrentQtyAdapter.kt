package my.edu.tarc.warehouserit3g2.currectQty

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.warehouserit3g2.Models.ViewModel
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.person.EmployeeProfile_Fragment
import my.edu.tarc.warehouserit3g2.stockInOut.Stock
import my.edu.tarc.warehouserit3g2.stockInOut.StockOut_FragmentDirections

class CurrentQtyAdapter (private val CurrentQtyList : ArrayList<CurrentQty>, private var fm : FragmentManager) : RecyclerView.Adapter<CurrentQtyAdapter.myViewHolder>() {

    private lateinit var person: ViewModel

    class myViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partNo: TextView = itemView.findViewById(R.id.currentqtypartno)
        val qty: TextView = itemView.findViewById(R.id.CurQty)
        val probar: ProgressBar = itemView.findViewById(R.id.progressBarCurQty)
        val per: TextView = itemView.findViewById(R.id.QtyPercentage)
        val warning : TextView = itemView.findViewById(R.id.warning)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.current_qty_item, parent, false)
        return myViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        person = ViewModel.getInstance()
        val CurrentQty = CurrentQtyList[position]

        holder.partNo.text = CurrentQty.PartNo
        holder.qty.text = CurrentQty.Qty
        holder.probar.progress = CurrentQty.progress.toInt()
        holder.per.text = String.format ("%.2f",CurrentQty.progress) + "%"
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

        holder.itemView.setOnClickListener {
            val partNo = CurrentQty.PartNo
            person.setMin(partNo)
            var dialog = SetQuantityMin_Fragment()
            dialog.show(fm,"setMinQty")
        }

    }

    override fun getItemCount(): Int {
        return CurrentQtyList.size

    }

}