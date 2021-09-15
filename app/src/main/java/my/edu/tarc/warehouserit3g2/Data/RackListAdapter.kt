package my.edu.tarc.warehouserit3g2.Data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.warehouserit3g2.R
import my.edu.tarc.warehouserit3g2.changeRack.RackList_Fragment

class RackListAdapter ( private var rackList: ArrayList<String>, private val listener: RackList_Fragment
) : RecyclerView.Adapter<RackListAdapter.myViewHolder>(), Filterable {

    var searchV = ArrayList<String>();

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        val rackid: TextView = itemView.findViewById(R.id.RackID)

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
        searchV = rackList
    }

    interface OnItemClickListener{

        fun onItemClick(position: Int)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.display_rack, parent, false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        val currentRack = searchV[position]
//        holder.itemView.setOnClickListener(
        holder.rackid.text = currentRack

    }

    override fun getItemCount(): Int {
        return searchV.size
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {

                    searchV = rackList


                } else {
                    val resultList = ArrayList<String>()
                    for (row in rackList) {

                        if(row.contains(charSearch) || row.contains(charSearch)){

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

                searchV = filterResults!!.values as ArrayList<String>
                notifyDataSetChanged()
            }

        }
    }
}