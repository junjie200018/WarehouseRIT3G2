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

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val partNo:TextView = itemView.findViewById(R.id.PartNo)
        val serialNo:TextView =itemView.findViewById(R.id.SerialNo)
        val quanti:TextView = itemView.findViewById(R.id.quantity)
        val receivedD:TextView = itemView.findViewById(R.id.receivedDate)


        init {

            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {

            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    init {
        searchV = productList as ArrayList<Product>
    }

    // click function of the recycleview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.received_item, parent, false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {


        val currentProduct = searchV[position]

        holder.partNo.text = currentProduct.partNo
        holder.serialNo.text = currentProduct.SerialNo
        holder.quanti.text = currentProduct.quantity
        holder.receivedD.text = currentProduct.date
    }

    override fun getItemCount(): Int {

        return searchV.size
    }


    interface OnItemClickListener{

        fun onItemClick(position: Int)

    }

    //search function of the recycleview
    override fun getFilter(): Filter {

        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {

                    searchV = productList as ArrayList<Product>


                } else {
                    val resultList = ArrayList<Product>()
                    for (row in productList) {

                       if(row.partNo.contains(charSearch) || row.SerialNo.contains(charSearch) || row.date.contains(charSearch) || row.quantity.contains(charSearch)){

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

                searchV = filterResults!!.values as ArrayList<Product>
                notifyDataSetChanged()
            }

        }
    }



}