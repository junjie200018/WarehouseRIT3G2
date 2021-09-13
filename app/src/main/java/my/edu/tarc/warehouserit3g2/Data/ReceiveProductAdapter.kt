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
import my.edu.tarc.warehouserit3g2.OnRack_Display_Fragment
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.ReceiveProductList_Fragment
import my.edu.tarc.warehouserit3g2.receiveProduct_Fragment

class ReceiveProductAdapter(
    private var receiveProductList: ArrayList<newProductBarcode>, private val listener: ReceiveProductList_Fragment
) : RecyclerView.Adapter<ReceiveProductAdapter.myViewHolder>(), Filterable {
    var searchV = ArrayList<newProductBarcode>();

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val partNo: TextView = itemView.findViewById(R.id.receivePartNo)
        val quantity: TextView = itemView.findViewById(R.id.receiveQuantity)
        val barcodeValue: TextView = itemView.findViewById(R.id.barcodeNoValue)
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
        searchV = receiveProductList as ArrayList<newProductBarcode>
    }

    interface OnItemClickListener{

        fun onItemClick(position: Int)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        Log.w(ContentValues.TAG, "search value 42 = ")
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.display_receive_product, parent, false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        Log.w(ContentValues.TAG, "search value 49 = ")

        val current = searchV[position]
//        holder.itemView.setOnClickListener(
        holder.partNo.text = current.partNo
        holder.quantity.text = current.quantity
        holder.barcodeValue.text= current.barodeNo

    }

    override fun getItemCount(): Int {
        return searchV.size
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                Log.w(ContentValues.TAG, "search value 75 = ${charSearch} ")
                if (charSearch.isEmpty()) {
                    Log.w(ContentValues.TAG, "77 ")
                    searchV = receiveProductList


                } else {
                    val resultList = ArrayList<newProductBarcode>()
                    for (row in receiveProductList) {
                        Log.w(ContentValues.TAG, " 999 ")
                        if(row.partNo.contains(charSearch) || row.quantity.contains(charSearch) || row.barodeNo.contains(charSearch)){
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
                searchV = filterResults!!.values as ArrayList<newProductBarcode>
                notifyDataSetChanged()
            }

        }
    }

}