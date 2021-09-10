package my.edu.tarc.warehouserit3g2.Data

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.warehouserit3g2.R

class TransitAdapter (private val transitList :List<Transit>, private val listener :OnItemClickListener) : RecyclerView.Adapter<TransitAdapter.myViewHolder>() {

    inner class myViewHolder (itemView :View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val id :TextView = itemView.findViewById(R.id.tvId)
        val from :TextView = itemView.findViewById(R.id.tvFrom)
        val to :TextView = itemView.findViewById(R.id.tvTo)


        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0 :View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transit_item,parent,false)

        return myViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentTransit = transitList[position]
        holder.id.text = currentTransit.id
        holder.from.text = currentTransit.from
        holder.to.text = currentTransit.to
    }

    override fun getItemCount(): Int {
        return transitList.size
    }

    interface OnItemClickListener{

        fun onItemClick(position :Int)
    }
}