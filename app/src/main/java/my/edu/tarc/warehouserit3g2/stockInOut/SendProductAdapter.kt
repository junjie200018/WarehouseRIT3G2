package my.edu.tarc.warehouserit3g2.stockInOut

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.warehouserit3g2.R

class SendProductAdapter (private val SendProductList : ArrayList<Stock>) : RecyclerView.Adapter<SendProductAdapter.myViewHolder>() {

    class myViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partNo: TextView = itemView.findViewById(R.id.PartNo1)
        val reason: TextView = itemView.findViewById(R.id.reason)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.stock_out_item, parent, false)
        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentRecProduct = SendProductList[position]
        holder.partNo.text = currentRecProduct.PartNo
        holder.reason.text = currentRecProduct.Status

        holder.itemView.setOnClickListener {
            val serialNo = currentRecProduct.SerialNo
            val action = StockOut_FragmentDirections.actionStockOutFragmentToStockDetailFragment(serialNo)
            Navigation.findNavController(it).navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return SendProductList.size

    }

}