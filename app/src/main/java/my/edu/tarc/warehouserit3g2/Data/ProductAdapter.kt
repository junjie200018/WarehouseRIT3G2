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

class ProductAdapter (private val productList :MutableList<Product>, private val listener: OnItemClickListener ) : RecyclerView.Adapter<ProductAdapter.myViewHolder>(), Filterable{

    var searchV = ArrayList<Product>();
    fun setData(searchV: ArrayList<Product>){
        this.searchV = searchV
        notifyDataSetChanged()
    }
    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val partNo:TextView = itemView.findViewById(R.id.PartNo)
        val serialNo:TextView =itemView.findViewById(R.id.SerialNo)


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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.received_item, parent, false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentProduct = productList[position]
//        holder.itemView.setOnClickListener(
        holder.partNo.text = currentProduct.partNo
        holder.serialNo.text = currentProduct.SerialNo
    }

    override fun getItemCount(): Int {
        return productList.size
    }


    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                Log.w(ContentValues.TAG, "search value 2 = ${charSequence}")
                val filterResult = FilterResults();

                if(charSequence == null || charSequence.length < 0){
                    filterResult.count = searchV.size
                    filterResult.values = searchV
                }else{
                    var searchChr = charSequence.toString()
                    val itemModal = ArrayList<Product>()
                    Log.w(ContentValues.TAG, "search value 3 = ${searchChr}")

                    for(item in searchV){
                        if(item.partNo.contains(searchChr) || item.SerialNo.contains(searchChr)){
                            itemModal.add(item)
                        }
                    }
                    filterResult.count = itemModal.size
                    filterResult.values = itemModal
                }
                    return filterResult
            }

            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                searchV = filterResults!!.values as ArrayList<Product>
                notifyDataSetChanged()
            }

        }
    }



}