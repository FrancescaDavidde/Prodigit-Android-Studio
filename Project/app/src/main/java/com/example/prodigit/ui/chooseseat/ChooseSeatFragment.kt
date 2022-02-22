package com.example.prodigit.ui.chooseseat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.prodigit.DatabaseManager
import com.example.prodigit.R
import com.example.prodigit.ui.ReservationViewModel
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.AdapterView.OnItemSelectedListener

import android.R.string.no
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController


class ChooseSeatFragment : Fragment() {

    private val ReservationViewModel: ReservationViewModel by activityViewModels()
    private val aula_codes= arrayOf("ROOM1","ROOM2","ROOM3","ROOM4","ROOM5")
    /*
    private val hours_from= arrayOf("----","8:00","9:00","10:00","11:00","12:00","13:00","14:00",
                                    "15:00","16:00","17:00")
    private val hours_to= arrayOf("----","9:00","10:00","11:00","12:00","13:00","14:00",
                                    "15:00","16:00","17:00")
     */

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference


    @SuppressLint("UseRequireInsteadOfGet", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //ReservationViewModel =
        //    ViewModelProvider(this).get(ReservationViewModel::class.java)

        val view = inflater.inflate(R.layout.choose_seat_fragment,container,false)


        //Variables
        val spinner_date = view.findViewById<Spinner>(R.id.spinner_date)
        val spinner_room = view.findViewById<Spinner>(R.id.spinner_room)
        val declaration = view.findViewById<Button>(R.id.go_to_declaration)
        val text_show = view.findViewById<TextView>(R.id.seats_available)
        val check_declaration = view.findViewById<CheckBox>(R.id.dichiarazione_resp)
        val check_green_pass = view.findViewById<CheckBox>(R.id.dichiarazione_greenpass)

        declaration.setOnClickListener {
            findNavController().navigate(R.id.action_chooseSeat_to_declarationLiability)
        }

        //Set Adapter for Spinner Room
        val adapter = ArrayAdapter(activity!!,android.R.layout.simple_spinner_item,aula_codes.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_room?.setAdapter(adapter)

        //Date handle
        val next_week_days = mutableListOf<String>()
        val format1 = SimpleDateFormat("dd-MM-yyyy")
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)
        val formatted = format1.format(cal.time)
        Log.d("DATE NOW",formatted)
        for(i in 7..12){
            val cal: Calendar = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY)
            cal.add(Calendar.DATE, i)
            val formatted2 = format1.format(cal.time)
            Log.d("NEXT WEEK DATE",formatted2)
            next_week_days.add(formatted2)
        }
        Log.d("PRINT_LIST","$next_week_days")

        //Set adapter for Choose Day
        val adapter_days =ArrayAdapter(activity!!,android.R.layout.simple_spinner_item,next_week_days)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_date?.setAdapter(adapter_days)

        //Set adapter for Spinner hours
        /*
        val adapter_hour =ArrayAdapter(activity!!,android.R.layout.simple_spinner_item,hours_from.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view?.findViewById<Spinner>(R.id.spinner_hour_from1)?.setAdapter(adapter_hour)
        view?.findViewById<Spinner>(R.id.spinner_hour_from2)?.setAdapter(adapter_hour)
        view?.findViewById<Spinner>(R.id.spinner_hour_from3)?.setAdapter(adapter_hour)
        view?.findViewById<Spinner>(R.id.spinner_hour_from4)?.setAdapter(adapter_hour)
        view?.findViewById<Spinner>(R.id.spinner_hour_from5)?.setAdapter(adapter_hour)
        view?.findViewById<Spinner>(R.id.spinner_hour_from6)?.setAdapter(adapter_hour)

        val adapter_to =ArrayAdapter(activity!!,android.R.layout.simple_spinner_item,hours_to.toList())
        view?.findViewById<Spinner>(R.id.spinner_hour_to1)?.setAdapter(adapter_to)
        view?.findViewById<Spinner>(R.id.spinner_hour_to2)?.setAdapter(adapter_to)
        view?.findViewById<Spinner>(R.id.spinner_hour_to3)?.setAdapter(adapter_to)
        view?.findViewById<Spinner>(R.id.spinner_hour_to4)?.setAdapter(adapter_to)
        view?.findViewById<Spinner>(R.id.spinner_hour_to5)?.setAdapter(adapter_to)
        view?.findViewById<Spinner>(R.id.spinner_hour_to6)?.setAdapter(adapter_to)
        */

        //Set values of Code and Name layouts
        view?.findViewById<TextView>(R.id.sede_ubicazione)?.text=ReservationViewModel.name_building.value
        view?.findViewById<TextView>(R.id.BuildingCode)?.text=ReservationViewModel.code_building.value
        ReservationViewModel.code_building.value?.let { Log.i("CHECK_VALUE", it) }

        //Number of seat available
        val database_manager = DatabaseManager()
        database_manager.checkCapacity(ReservationViewModel.code_building.value,spinner_date?.selectedItem.toString(),
            spinner_room?.selectedItem.toString(),context,view.findViewById<TextView>(R.id.num_seats_available),text_show)

        //Reservation
        view?.findViewById<Button>(R.id.book_button)?.setOnClickListener{
            /*
            val database_manager = DatabaseManager()
            database_manager.checkCapacity(ReservationViewModel.code_building.value,view.findViewById<Spinner>(R.id.spinner_date)?.selectedItem.toString(),
                view.findViewById<Spinner>(R.id.spinner_room)?.selectedItem.toString(),context,view.findViewById<TextView>(R.id.num_seats_available))

             */

            if(check_green_pass.isChecked && check_declaration.isChecked) {

                if (text_show.text.equals("Seats available:") && view.findViewById<TextView>(R.id.num_seats_available)?.text.toString()
                        .toInt() != 0
                ) {
                    val database_manager = DatabaseManager()
                    database_manager.addNewReservation(
                        ReservationViewModel.code_building.value,
                        spinner_room?.selectedItem.toString(),
                        spinner_date?.selectedItem.toString()
                    )
                    val builder = context?.let { it1 ->
                        AlertDialog.Builder(
                            it1
                        )
                    }
                    builder?.setTitle("RESERVATION MADE")
                    builder?.setMessage("Your seat has been saved!")
                    builder?.setPositiveButton("Ok") { _, _ -> }
                    builder?.show()
                } else if (text_show.text != "Seats available:") {
                    val builder = context?.let { it1 ->
                        AlertDialog.Builder(
                            it1
                        )
                    }
                    builder?.setTitle("ALREADY BOOKED")
                    builder?.setMessage("You are already booked for that room in that date")
                    builder?.setPositiveButton("Ok") { _, _ -> }
                    builder?.show()
                } else {
                    val builder = context?.let { it1 ->
                        AlertDialog.Builder(
                            it1
                        )
                    }
                    builder?.setTitle("NO SEATS AVAILABLE")
                    builder?.setMessage("we are sorry but there are no seats left for that date")
                    builder?.setPositiveButton("Ok") { _, _ -> }
                    builder?.show()
                }

            }else if(check_green_pass.isChecked && !check_declaration.isChecked){
                val builder = context?.let { it1 ->
                    AlertDialog.Builder(
                        it1
                    )
                }
                builder?.setTitle("DECLARATION NEEDED")
                builder?.setMessage("You need to accept the Declaration of Liability")
                builder?.setPositiveButton("Ok") { _, _ -> }
                builder?.show()
            }else{
                val builder = context?.let { it1 ->
                    AlertDialog.Builder(
                        it1
                    )
                }
                builder?.setTitle("GREEN PASS NEEDED")
                builder?.setMessage("You need to have a valid green pass(or non-UE equivalent)")
                builder?.setPositiveButton("Ok") { _, _ -> }
                builder?.show()
            }
            /*
            Log.d("REMAINING_SEATS",database_manager.counter_capacity.toString())
            if(database_manager.counter_capacity<3) {
                database_manager.addNewReservation(
                    ReservationViewModel.code_building.value,
                    view.findViewById<Spinner>(R.id.spinner_room)?.selectedItem.toString(),
                    view.findViewById<Spinner>(R.id.spinner_date)?.selectedItem.toString()
                )
            }else{
                val builder = getContext()?.let { it1 -> AlertDialog.Builder(it1) }
                builder?.setTitle("NO SEATS AVAILABLE")
                builder?.setMessage("we are sorry but there are no seats left for that date")
            }
             */
        }

        //Listener to onChange of spinners
        spinner_date.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view2: View?,
                position: Int,
                id: Long
            ) {
                Log.d("CHECK_SPINNER_SELECTED","IN FUNCTION")
                val database_manager = DatabaseManager()
                database_manager.checkCapacity(ReservationViewModel.code_building.value,spinner_date?.selectedItem.toString(),
                    spinner_room?.selectedItem.toString(),context,view.findViewById<TextView>(R.id.num_seats_available), text_show)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        //Same for spinner room
        spinner_room.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view2: View?,
                position: Int,
                id: Long
            ) {
                database_manager.checkCapacity(ReservationViewModel.code_building.value,spinner_date?.selectedItem.toString(),
                    spinner_room?.selectedItem.toString(),context,view.findViewById<TextView>(R.id.num_seats_available),text_show)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        //Add on change listener to update the seats available
        val ref= database.child("reservations")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                database_manager.checkCapacity(ReservationViewModel.code_building.value,spinner_date?.selectedItem.toString(),
                    spinner_room?.selectedItem.toString(),context,view.findViewById<TextView>(R.id.num_seats_available),text_show)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ON_DATA_CHANGE", "loadPost:onCancelled", databaseError.toException())
            }
        }
        ref.addValueEventListener(postListener)

        return view
    }

}