package com.example.prodigit

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.prodigit.models.ReservationModel
import com.example.prodigit.ui.ReservationViewModel
import com.example.prodigit.ui.myreservation.ResAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.lang.Thread.sleep

class DatabaseManager (private val database: DatabaseReference? = FirebaseDatabase.getInstance().reference,
                       private val uid: String? = Firebase.auth.currentUser!!.uid) {

    private val dictionary_code_position = mutableMapOf<String, String>(
        "CU001" to "Piazzale Aldo Moro,5",
        "CU002" to "Piazzale Aldo Moro,5",
        "CU003" to "Piazzale Aldo Moro,5",
        "RM100" to "Viale Regina Elena 287",
        "RM101" to "Via Caserta, 6",
        "RM102" to "Via Ariosto, 25",
        "LT002" to "Viale XXIV Maggio n.7 Latina"
    )

    fun addNewReservation(BuildingCode: String?, RoomCode: String?, Date: String?) {
        val Summary = BuildingCode+"_"+RoomCode+"_"+Date
        val reservation = ReservationModel(
            BuildingCode,
            RoomCode,
            Date,
            dictionary_code_position.get(BuildingCode),
            Summary
        )
        Log.d("UID", uid!!)
        var num_res = 0
        database!!.child("users").child(uid!!).get().addOnSuccessListener {
            Log.d("NUMRES", "true")
            Log.d("NUMRES", it.child("numRes").value.toString())
            num_res = it.child("numRes").value.toString().toInt()
            //Add new reservation with Res Number num_res
            database!!.child("reservations").child(uid!!)
                .child((it.child("numRes").value.toString().toInt() + 1).toString())
                .setValue(reservation)
            //Update numRes in the users table
            database!!.child("users").child(uid!!).child("numRes")
                .setValue(it.child("numRes").value.toString().toInt() + 1)
        }
    }

    fun addNewUser() {
        Log.d("ADD_USER", "HERE")
        val uid = Firebase.auth.currentUser!!.uid
        Log.d("ADD_USER", uid.toString())
        database!!.child("users").child(uid!!).get().addOnSuccessListener {
            if (it.hasChildren()) {
                //If the user already exists do nothing (for the Login with Google)
                Log.d("CHECK_EXIST", "EXISTS")
            } else {
                //Otherwise create instance on the database
                Log.d("CHECK_EXIST", "NOT_EXISTS")
                database!!.child("users").child(uid!!).child("numRes").setValue(0)
            }
        }
        //database!!.child("users").child(uid!!).child("numRes").setValue(0)
    }

    fun removeAccount() {
        val uid = Firebase.auth.currentUser!!.uid
        database!!.child("users").child(uid!!).removeValue()
        database!!.child("reservations").child(uid!!).removeValue()
    }

    fun checkCapacity(
        BuildingCode: String?,
        Date: String?,
        RoomCode: String?,
        context: Context?,
        num_available: TextView?,
        seats_available: TextView?
    ) {
        var count: Int = 0
        var check_seats= 0
        database!!.child("reservations").get().addOnSuccessListener {
            if (it.hasChildren()) {
                for (user in it.children) {
                    //Log.d("USERS_CHECK",user.toString())
                    Log.d("USERS_CHECK", user.key.toString())
                    database!!.child("reservations").child(user.key.toString()).get()
                        .addOnSuccessListener {
                            if (it.hasChildren()) {
                                for (reservation in it.children) {
                                    if (reservation.child("buildingCode").value.toString()
                                            .equals(BuildingCode)
                                        && reservation.child("date").value.toString().equals(Date)
                                        && reservation.child("roomCode").value.toString()
                                            .equals(RoomCode)
                                    ) {
                                        Log.d("EQUALS", "HERE")
                                        count += 1
                                    }

                                    if(reservation.child("summary").value.toString().equals(BuildingCode+"_"+RoomCode+"_"+Date) &&
                                            user.key.toString().equals(uid.toString())){
                                            check_seats=1
                                    }
                                }
                                Log.d("COUNT", count.toString())
                                if(check_seats==1){
                                    num_available?.text =""
                                    seats_available?.setText(R.string.already_booked)
                                    seats_available?.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16f)
                                }
                                else{
                                    seats_available?.text="Seats available:"
                                    seats_available?.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18f)
                                    num_available?.text = (20 - count).toString()
                                }
                                /*
                            if(count<20){
                                addNewReservation(BuildingCode,RoomCode,Date)
                            }else{
                                val builder = context?.let { it1 ->
                                    androidx.appcompat.app.AlertDialog.Builder(
                                        it1
                                    )
                                }
                                builder?.setTitle("NO SEATS AVAILABLE")
                                builder?.setMessage("we are sorry but there are no seats left for that date")
                                builder?.show()
                            }
                             */
                            }
                        }
                }
            }
        }.addOnFailureListener {
            Log.e("CHECK_CAPACITY", "error", it)
        }
    }

    fun getReservationList(fragment: Fragment,view: View) {
        val listReservation: MutableList<ReservationModel> = mutableListOf()
        val lm: List<ReservationModel> = listReservation

        database!!.child("reservations").get().addOnSuccessListener {
            var check=0
            if (it.hasChildren()) {
                for (i in it.children) {
                    Log.d("USERID: ",i.toString())
                    if (i.key.toString() == uid!!.toString()) {
                        check=1
                    }
                }
            }
            if(check==0){
                view.findViewById<TextView>(R.id.textView5).setVisibility(View.VISIBLE)
            }else{
                view.findViewById<TextView>(R.id.textView5).setVisibility(View.INVISIBLE)
            }
        }

        database!!.child("reservations").child(uid!!).get().addOnSuccessListener {
            if (it.hasChildren()) {
                for (i in it.children) {
                    val date = i.child("date").value.toString()
                    val building = i.child("buildingCode").value.toString()
                    val room = i.child("roomCode").value.toString()
                    val summary=i.child("summary").value.toString()
                    Log.d("RES_ID",i.key.toString())
                    listReservation.add(ReservationModel(BuildingCode = building, RoomCode = room, Date = date,Id=i.key.toString()))
                }

                var myDataset = listReservation
                //myDataset.sortByDescending { it -> it.date }
                fragment.requireView().findViewById<RecyclerView>(R.id.my_reservation_recycle).apply {
                    layoutManager = LinearLayoutManager(fragment.activity)
                    adapter = ResAdapter(this.context, myDataset as ArrayList<ReservationModel>, fragment,view)
                }
            }
        }.addOnFailureListener {
            Log.e("firebase", "Error getting users", it)
        }
    }

    fun deleteReservation(ReservationID: String,view:View){
        val uid = Firebase.auth.currentUser!!.uid
        //var num_res = 0
        database!!.child("users").child(uid!!).get().addOnSuccessListener {
            Log.d("NUMRES", "true")
            Log.d("NUMRES", it.child("numRes").value.toString())
            //num_res = it.child("numRes").value.toString().toInt()
            //Add new reservation with Res Number num_res
            database!!.child("reservations").child(uid!!)
                .child(ReservationID).removeValue()
            //Update numRes in the users table
            database!!.child("users").child(uid!!).child("numRes")
                .setValue(it.child("numRes").value.toString().toInt() - 1)

        }
    }

}