package my.edu.tarc.warehouserit3g2.searchStock

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.stockInOut.Stock
import java.util.*
import kotlin.collections.ArrayList

class SearchStockAdapter (private val SearchProductList : ArrayList<Stock>) : RecyclerView.Adapter<SearchStockAdapter.myViewHolder>(), 
    Filterable {

    var searchProductFilterList = ArrayList<Stock>()

    inner class myViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partNo: TextView = itemView.findViewById(R.id.PartNoS)
        val serialNo: TextView = itemView.findViewById(R.id.SerialNoS)
        val recBy: TextView = itemView.findViewById(R.id.recByS)

    }

    init {
        searchProductFilterList = SearchProductList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_stock_item, parent, false)
        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentRecProduct = searchProductFilterList[position]
        holder.partNo.text = currentRecProduct.PartNo
        holder.recBy.text = currentRecProduct.RecBy
        holder.serialNo.text = currentRecProduct.SerialNo

        holder.itemView.setOnClickListener {
            val serialNo = currentRecProduct.SerialNo
            val action = SearchStock_FragmentDirections.actionSearchStockFragmentToStockDetailFragment(serialNo)
            Navigation.findNavController(it).navigate(action)

        }

    }

    override fun getItemCount(): Int {
        return searchProductFilterList.size

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    searchProductFilterList = SearchProductList
                } else {
                    val resultList = ArrayList<Stock>()
                    for (row in SearchProductList) {
                        if (row.PartNo.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT)) ||
                            row.SerialNo.lowercase(Locale.ROOT).contains(charSearch.lowercase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    searchProductFilterList  = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = searchProductFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                searchProductFilterList = results?.values as ArrayList<Stock>
                notifyDataSetChanged()
            }

        }
    }

}