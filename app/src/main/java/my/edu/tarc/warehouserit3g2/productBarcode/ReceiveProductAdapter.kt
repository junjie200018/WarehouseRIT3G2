package my.edu.tarc.warehouserit3g2.productBarcode

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.warehouserit3g2.R

class ReceiveProductAdapter(
    private var receiveProductList: ArrayList<newProductBarcode>, private val listener: ProductList_Fragment
) : RecyclerView.Adapter<ReceiveProductAdapter.myViewHolder>(), Filterable {
    var searchV = ArrayList<newProductBarcode>();

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val partNo: TextView = itemView.findViewById(R.id.receivePartNo)
        val quantity: TextView = itemView.findViewById(R.id.receiveQuantity)
        val barcodeValue: TextView = itemView.findViewById(R.id.barcodeNoValue)
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
        searchV = receiveProductList as ArrayList<newProductBarcode>
    }

    interface OnItemClickListener{

        fun onItemClick(position: Int)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.display_receive_product, parent, false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {


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

                if (charSearch.isEmpty()) {

                    searchV = receiveProductList


                } else {
                    val resultList = ArrayList<newProductBarcode>()
                    for (row in receiveProductList) {

                        if(row.partNo.contains(charSearch.uppercase()) || row.quantity.contains(charSearch.uppercase()) || row.barodeNo.contains(charSearch.uppercase())){

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

                searchV = filterResults!!.values as ArrayList<newProductBarcode>
                notifyDataSetChanged()
            }

        }
    }

}