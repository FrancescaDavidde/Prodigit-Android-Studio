package com.example.prodigit.ui.newreservation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.prodigit.R
import com.example.prodigit.ui.ReservationViewModel


class NewReservationFragment : Fragment() {

    private val ReservationViewModel: ReservationViewModel by activityViewModels()

    private val building_codes= arrayOf("CU001","CU002","CU003","RM100","RM101","RM102","LT002")
    private val building_names= arrayOf("Rettorato","Giurisprudenza,Sc.politiche e statistiche","Lettere e Filosofia",
                                        "Clinica Odontoiatrica","Clinica Odont(Polo Didattico)","Via Ariosto","Economia Latina")

    private val dictionary_code_name = mutableMapOf<String,String>(
        "CU001" to "Rettorato",
        "CU002" to "Giurisprudenza,Sc.politiche e statistiche",
        "CU003" to "Lettere e Filosofia",
        "RM100" to "Clinica Odontoiatrica",
        "RM101" to "Clinica Odont(Polo Didattico)",
        "RM102" to "Via Ariosto",
        "LT002" to "Economia Latina"
    )

    private val dictionary_name_code = mutableMapOf<String,String>(
        "Rettorato" to "CU001",
        "Giurisprudenza,Sc.politiche e statistiche" to "CU002",
        "Lettere e Filosofia" to "CU003",
        "Clinica Odontoiatrica" to "RM100",
        "Clinica Odont(Polo Didattico)" to "RM101",
        "Via Ariosto" to "RM102",
        "Economia Latina" to "LT002"
    )

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //view?.findViewById<AutoCompleteTextView>(R.id.spinner_name)?.threshold=1

        //ReservationViewModel =
        //    ViewModelProvider(this).get(ReservationViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_newreservation, container, false)

        val code_view = view?.findViewById<AutoCompleteTextView>(R.id.spinner_code)
        val name_view = view?.findViewById<AutoCompleteTextView>(R.id.spinner_name)

        code_view?.setOnFocusChangeListener({ view: View, b: Boolean ->
            code_view.showDropDown()
        })


        name_view?.setOnFocusChangeListener({ view: View, b: Boolean ->
            name_view.showDropDown()
        })

        //Handle access to ChooseSeat
        view.findViewById<Button>(R.id.search_button).setOnClickListener {
            Log.i("HERE","QUI")
            val code = view?.findViewById<AutoCompleteTextView>(R.id.spinner_code)?.text.toString()
            val name = view?.findViewById<AutoCompleteTextView>(R.id.spinner_name)?.text.toString()
            if(code.isEmpty() && name.isEmpty()){
                Log.d("CHECK_EMPYT","YES")
                val toast = Toast.makeText(
                    context, "Insert Building Name or Code",
                    Toast.LENGTH_SHORT
                )
                toast.view?.setBackgroundResource(R.drawable.toast_style)
                toast.show()
            } else if(name!=dictionary_code_name.get(code) && code.isNotEmpty() && name.isNotEmpty()){
                Log.d("CHECK_EQUAL","NO")
                val toast = Toast.makeText(
                    context, "Mismatch in Code and Name of the Building",
                    Toast.LENGTH_SHORT
                )
                toast.view?.setBackgroundResource(R.drawable.toast_style)
                toast.show()
            }else if(code.isEmpty()){
                if(building_names.contains(name)) {
                    ReservationViewModel.setBuildingName(name)
                    dictionary_name_code.get(name)
                        ?.let { it1 -> ReservationViewModel.setBuildingCode(it1) }
                    findNavController().navigate(R.id.action_newReservationFragment_to_chooseSeatFragment)
                }else{
                    val toast = Toast.makeText(
                        context, "Name of the Building not valid",
                        Toast.LENGTH_SHORT
                    )
                    toast.view?.setBackgroundResource(R.drawable.toast_style)
                    toast.show()
                }
            }
            else {
                if(building_codes.contains(code)) {
                    ReservationViewModel.setBuildingCode(code)
                    dictionary_code_name.get(code)
                        ?.let { it1 -> ReservationViewModel.setBuildingName(it1) }
                    findNavController().navigate(R.id.action_newReservationFragment_to_chooseSeatFragment)
                }else{
                    val toast = Toast.makeText(
                        context, "Code of the Building not valid",
                        Toast.LENGTH_SHORT
                    )
                    toast.view?.setBackgroundResource(R.drawable.toast_style)
                    toast.show()
                }
            }
        }

        val adapter = ArrayAdapter(activity!!,android.R.layout.simple_dropdown_item_1line,building_codes.toList())
        view?.findViewById<AutoCompleteTextView>(R.id.spinner_code)?.setAdapter(adapter)

        val adapter2 = ArrayAdapter(activity!!,android.R.layout.simple_dropdown_item_1line,building_names.toList())
        view?.findViewById<AutoCompleteTextView>(R.id.spinner_name)?.setAdapter(adapter2)

        return view
    }


}