package my.edu.tarc.warehouserit3g2.transit


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.warehouserit3g2.Data.DisplayTransit
import my.edu.tarc.warehouserit3g2.R


class DisplayTransitAdapter(
    private var TransitProductList: MutableList<DisplayTransit>,
    private val listener: DisplayTransit_Fragment
) : RecyclerView.Adapter<DisplayTransitAdapter.myViewHolder>(), Filterable {

    var searchV = ArrayList<DisplayTransit>();

    inner class myViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val partNo: TextView = itemView.findViewById(R.id.TransitPartNo)
        val quantity: TextView = itemView.findViewById(R.id.TransitQuantity)
        val destination: TextView = itemView.findViewById(R.id.TransitDestination)
        val from: TextView = itemView.findViewById(R.id.TransitFrom)

    }

    init {
        searchV = TransitProductList as ArrayList<DisplayTransit>
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.display_transit, parent, false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {


        val current = searchV[position]
        holder.partNo.text = current.partNo
        holder.quantity.text = current.quantity
        holder.destination.text = current.destination
        holder.from.text = current.from

    }

    override fun getItemCount(): Int {
        return searchV.size
    }

    // search of the recycleview
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()

                if (charSearch.isEmpty()) {

                    searchV = TransitProductList as ArrayList<DisplayTransit>


                } else {
                    val resultList = ArrayList<DisplayTransit>()
                    for (row in TransitProductList) {

                        if (row.partNo.contains(charSearch.uppercase()) || row.quantity.contains(charSearch.uppercase()) || row.destination.contains(charSearch.uppercase())
                            || row.from.contains(charSearch.uppercase())) {

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

                searchV = filterResults!!.values as ArrayList<DisplayTransit>
                notifyDataSetChanged()
            }

        }
    }
}