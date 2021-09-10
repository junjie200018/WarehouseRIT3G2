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
import my.edu.tarc.warehouserit3g2.OnRack_Product_Display_Fragment
import my.edu.tarc.warehouserit3g2.R

class RackProductAdapter(private var productList:MutableList<Product>, private val listener: OnRack_Product_Display_Fragment) : RecyclerView.Adapter<RackProductAdapter.myViewHolder>(),
    Filterable {

    var searchV = ArrayList<Product>();

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val OnRackPartNo: TextView = itemView.findViewById(R.id.OnRackPartNo)
        val OnRackSerialNo: TextView =itemView.findViewById(R.id.OnRackSerialNo)


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
        searchV = productList as ArrayList<Product>
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
    }

    override fun getItemCount(): Int {
        Log.w(ContentValues.TAG, "search value 57 = ")
        return searchV.size
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                Log.w(ContentValues.TAG, "search value 75 = ${charSearch} ")
                if (charSearch.isEmpty()) {
                    Log.w(ContentValues.TAG, "77 ")
                    searchV = productList as ArrayList<Product>


                } else {
                    val resultList = ArrayList<Product>()
                    for (row in productList) {
                        Log.w(ContentValues.TAG, " 999 ")
                        if(row.partNo.contains(charSearch) || row.SerialNo.contains(charSearch)){
                            Log.w(ContentValues.TAG, "search value 83 = ${row} ")
                            resultList.add(row)
                        }
                    }
                    searchV = resultList
                    Log.w(ContentValues.TAG, "search value 88 = ${searchV} ")
                }
                val filterResults = FilterResults()
                filterResults.values = searchV
                Log.w(ContentValues.TAG, "Final2 = ${filterResults} ")
                return filterResults
            }


            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                Log.w(ContentValues.TAG, "final= ${filterResults} ")
                searchV = filterResults!!.values as ArrayList<Product>
                notifyDataSetChanged()
            }

        }
    }


}