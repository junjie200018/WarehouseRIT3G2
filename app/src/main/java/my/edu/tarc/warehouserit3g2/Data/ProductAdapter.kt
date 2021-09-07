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
import my.edu.tarc.warehouserit3g2.R
import java.util.*
import kotlin.collections.ArrayList

class ProductAdapter (private var productList :MutableList<Product>, private val listener: OnItemClickListener ) : RecyclerView.Adapter<ProductAdapter.myViewHolder>(), Filterable{

    var searchV = ArrayList<Product>();
//    fun setData(searchV: ArrayList<Product>){
//        this.searchV = searchV
//        notifyDataSetChanged()
//    }
    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val partNo:TextView = itemView.findViewById(R.id.PartNo)
        val serialNo:TextView =itemView.findViewById(R.id.SerialNo)


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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        Log.w(ContentValues.TAG, "search value 42 = ")
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.received_item, parent, false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        Log.w(ContentValues.TAG, "search value 49 = ")

        val currentProduct = searchV[position]
//        holder.itemView.setOnClickListener(
        holder.partNo.text = currentProduct.partNo
        holder.serialNo.text = currentProduct.SerialNo
    }

    override fun getItemCount(): Int {
        Log.w(ContentValues.TAG, "search value 57 = ")
        return searchV.size
    }


    interface OnItemClickListener{

        fun onItemClick(position: Int)

    }

    override fun getFilter(): Filter {
        Log.w(ContentValues.TAG, "search value 5 = ")
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
//            override fun performFiltering(charSequence: CharSequence?): FilterResults {
//                Log.w(ContentValues.TAG, "search value 2 = ${charSequence}")
//                val filterResult = FilterResults();
//
//                if(charSequence == null || charSequence.length < 0){
//                    filterResult.count = productList.size
//                    filterResult.values = productList
//                }else{
//                    var searchChr = charSequence.toString()
//                    val itemModal = ArrayList<Product>()
//                    Log.w(ContentValues.TAG, "search value 3 = ${searchChr}")
//
//                    for(item in productList){
//                        if(item.partNo.contains(searchChr) || item.SerialNo.contains(searchChr)){
//                            itemModal.add(item)
//                        }
//                    }
//                    filterResult.count = itemModal.size
//                    filterResult.values = itemModal
//                }
//                    return filterResult
//            }

            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                Log.w(ContentValues.TAG, "final= ${filterResults} ")
                searchV = filterResults!!.values as ArrayList<Product>
                notifyDataSetChanged()
            }

        }
    }



}