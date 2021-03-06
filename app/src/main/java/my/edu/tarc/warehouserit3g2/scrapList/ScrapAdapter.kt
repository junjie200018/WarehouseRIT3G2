package my.edu.tarc.warehouserit3g2.scrapList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.warehouserit3g2.Data.Product
import my.edu.tarc.warehouserit3g2.R

class ScrapAdapter (private var productList :MutableList<Product>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ScrapAdapter.myViewHolder>(),
    Filterable {
    var searchV = ArrayList<Product>();
    //    fun setData(searchV: ArrayList<Product>){
//        this.searchV = searchV
//        notifyDataSetChanged()
//    }
    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val partNo: TextView = itemView.findViewById(R.id.ScrapPartNo)
        val serialNo: TextView =itemView.findViewById(R.id.ScrapSerialNo)
        val rackOutDate: TextView = itemView.findViewById(R.id.rackOutDate)


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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.display_scrap, parent, false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {


        val currentProduct = searchV[position]
//        holder.itemView.setOnClickListener(
        holder.partNo.text = currentProduct.partNo
        holder.serialNo.text = currentProduct.SerialNo
        holder.rackOutDate.text = currentProduct.date
    }

    override fun getItemCount(): Int {

        return searchV.size
    }


    interface OnItemClickListener{

        fun onItemClick(position: Int)

    }

    override fun getFilter(): Filter {

        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {

                    searchV = productList as ArrayList<Product>


                } else {
                    val resultList = ArrayList<Product>()
                    for (row in productList) {

                        if(row.partNo.contains(charSearch.uppercase()) || row.SerialNo.contains(charSearch.uppercase()) || row.date.contains(charSearch.uppercase())){

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