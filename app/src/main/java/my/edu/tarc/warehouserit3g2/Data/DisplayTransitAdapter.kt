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
import my.edu.tarc.warehouserit3g2.transit.DisplayTransit_Fragment
import my.edu.tarc.warehouserit3g2.R


class DisplayTransitAdapter ( private var TransitProductList: MutableList<DisplayTransit>, private val listener: DisplayTransit_Fragment
) : RecyclerView.Adapter<DisplayTransitAdapter.myViewHolder>(), Filterable {

    var searchV = ArrayList<DisplayTransit>();

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val partNo: TextView = itemView.findViewById(R.id.TransitPartNo)
        val quantity: TextView = itemView.findViewById(R.id.TransitQuantity)
        val destination: TextView = itemView.findViewById(R.id.TransitDestination)
        val from: TextView = itemView.findViewById(R.id.TransitFrom)

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
        searchV = TransitProductList as ArrayList<DisplayTransit>
    }

    interface OnItemClickListener{

        fun onItemClick(position: Int)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        Log.w(ContentValues.TAG, "search value 42 = ")
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.display_transit, parent, false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
//        Log.w(ContentValues.TAG, "search value 49 = ${current.partNo}")

        val current = searchV[position]
        Log.w(ContentValues.TAG, "search value 49 = ${current.partNo}")
//        holder.itemView.setOnClickListener(
        holder.partNo.text = current.partNo
        holder.quantity.text = current.quantity
        holder.destination.text = current.destination
        holder.from.text = current.from

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
                    searchV = TransitProductList as ArrayList<DisplayTransit>


                } else {
                    val resultList = ArrayList<DisplayTransit>()
                    for (row in TransitProductList) {
                        Log.w(ContentValues.TAG, " 999 ")
                        if(row.partNo.contains(charSearch) || row.quantity.contains(charSearch) || row.destination.contains(charSearch)
                            || row.from.contains(charSearch)){

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
                searchV = filterResults!!.values as ArrayList<DisplayTransit>
                notifyDataSetChanged()
            }

        }
    }
}