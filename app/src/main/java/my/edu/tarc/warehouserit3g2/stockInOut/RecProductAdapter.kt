package my.edu.tarc.warehouserit3g2.stockInOut

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.warehouserit3g2.R

class RecProductAdapter (private val RecProductList : ArrayList<ReceivedProduct>) : RecyclerView.Adapter<RecProductAdapter.myViewHolder>() {

    class myViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partNo: TextView = itemView.findViewById(R.id.PartNo)
        val recDate: TextView = itemView.findViewById(R.id.recDate)
        val recBy: TextView = itemView.findViewById(R.id.recBy)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.stock_in_item, parent, false)
        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentRecProduct = RecProductList[position]
        holder.partNo.text = currentRecProduct.PartNo
        holder.recBy.text = currentRecProduct.RecBy
        holder.recDate.text = currentRecProduct.RecDate

        holder.itemView.setOnClickListener {
            val serialNo = currentRecProduct.SerialNo
            val action = StockIn_FragmentDirections.actionStockInFragmentToStockDetailFragment(serialNo)
            Navigation.findNavController(it).navigate(action)
        }

    }

    override fun getItemCount(): Int {
        return RecProductList.size

    }

}