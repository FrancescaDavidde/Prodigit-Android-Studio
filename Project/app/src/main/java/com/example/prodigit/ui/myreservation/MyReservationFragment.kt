package com.example.prodigit.ui.myreservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.prodigit.DatabaseManager
import com.example.prodigit.R

class MyReservationFragment : Fragment() {

    private lateinit var myReservationViewModel: MyReservationViewModel
    private val database = DatabaseManager()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myreservation, container, false)

        view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).isRefreshing = true
        database.getReservationList(this,view)
        view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).isRefreshing = false

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database.getReservationList(this,view)




        view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).setOnRefreshListener{
            view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).isRefreshing = true
            database.getReservationList(this,view)
            view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh).isRefreshing = false
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}