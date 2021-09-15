package my.edu.tarc.warehouserit3g2.Data

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.warehouserit3g2.displayRackItem.OnRack_Product_Display_Fragment
import my.edu.tarc.warehouserit3g2.R

class RackProductAdapter(private var productList:MutableList<RackProduct>, private val listener: OnRack_Product_Display_Fragment) : RecyclerView.Adapter<RackProductAdapter.myViewHolder>(),
    Filterable {

    var searchV = ArrayList<RackProduct>()

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val OnRackPartNo: TextView = itemView.findViewById(R.id.disPartNo)
        val OnRackSerialNo: TextView =itemView.findViewById(R.id.SerialNo)
        val OnRackRackID: TextView = itemView.findViewById(R.id.rackID)
        val OnRackQuantity: TextView = itemView.findViewById(R.id.quantity)
        val OnRackRackInD: TextView = itemView.findViewById(R.id.rackInDate)


        init {
            Log.w(ContentValues.TAG, "search value 30 = ")
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            Log.w(ContentValues.TAG, "search value 34 = ")
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    init {
        searchV = productList as ArrayList<RackProduct>
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        Log.w(ContentValues.TAG, "search value 42 = ")
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.display_rack_product, parent, false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        Log.w(ContentValues.TAG, "search value 49 = ")

        val currentProduct = searchV[position]
//        holder.itemView.setOnClickListener(
        holder.OnRackPartNo.text = currentProduct.partNo
        holder.OnRackSerialNo.text = currentProduct.SerialNo
        holder.OnRackQuantity.text = currentProduct.quantity
        holder.OnRackRackID.text = currentProduct.rackID
        holder.OnRackRackInD.text = currentProduct.rackInDate
    }

    override fun getItemCount(): Int {

        return searchV.size
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {

                    searchV = productList as ArrayList<RackProduct>


                } else {
                    val resultList = ArrayList<RackProduct>()
                    for (row in productList) {

                        if(row.partNo.contains(charSearch) || row.SerialNo.contains(charSearch) || row.quantity.contains(charSearch) ||
                            row.rackID.contains(charSearch) || row.rackInDate.contains(charSearch)){

                            resultList.add(row)
                        }
                    }
                    searchV = resultList

                }
                val filterResults = FilterResults()
                filterResults.values = searchV

                return filterResults
            }


            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {

                searchV = filterResults!!.values as ArrayList<RackProduct>
                notifyDataSetChanged()
            }

        }
    }


}