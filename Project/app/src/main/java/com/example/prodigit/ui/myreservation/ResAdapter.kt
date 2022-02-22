package com.example.prodigit.ui.myreservation

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.prodigit.DatabaseManager
import com.example.prodigit.R
import com.example.prodigit.models.ReservationModel

class ResAdapter(private val context: Context, private val res_list: ArrayList<ReservationModel>, private val fragment: Fragment,private val view:View)
    : RecyclerView.Adapter<ResAdapter.ItemViewHolder>()  {

    val database = DatabaseManager()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ResAdapter.ItemViewHolder, position: Int) {
        holder.bind(res_list[position],position)
    }

    override fun getItemCount(): Int {
        return res_list.size
    }

    fun deleteItem(index:Int,resId:String?){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("DELETE RESERVATION")
        builder.setMessage("Are you sure you want to delete this reservation?")

        builder.setPositiveButton("Yes") { _, _ ->
            res_list.removeAt(index)
            notifyDataSetChanged()
            if (resId != null) {
                database.deleteReservation(resId,view)
            }
            if(res_list.isEmpty()){
                view.findViewById<TextView>(R.id.textView5).setVisibility(View.VISIBLE)
            }
        }
        builder.setNegativeButton("No"){_,_ ->
            Log.d("DELETE_RES", "Not remove the reservation.")
        }
        builder.show()
    }

    inner class ItemViewHolder(private val itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(reservation:ReservationModel, index:Int) {
            val date: TextView = itemView.findViewById((R.id.tvdate))
            val building: TextView = itemView.findViewById((R.id.tvbuilding))
            val room: TextView = itemView.findViewById((R.id.tvroom))
            val delete_btn: ImageButton = itemView.findViewById((R.id.btnDeleteEvent))

            date.text = reservation.Date
            building.text = reservation.BuildingCode
            room.text = reservation.RoomCode

            delete_btn.setOnClickListener {
                deleteItem(index,reservation.Id)
            }
        }
    }
}