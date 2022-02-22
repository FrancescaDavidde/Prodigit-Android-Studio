package com.example.prodigit.ui.myreservation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyReservationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is myReservation Fragment"
    }
    val text: LiveData<String> = _text
}