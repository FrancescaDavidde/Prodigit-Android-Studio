package com.example.prodigit.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReservationViewModel : ViewModel() {

    private var _code_building = MutableLiveData<String>()
    val code_building: LiveData<String> = _code_building

    private var _name_building = MutableLiveData<String>()
    val name_building: LiveData<String> = _name_building

    private var _check = MutableLiveData<Int>()
    val check: LiveData<Int> = _check

    fun setBuildingCode(code:String){
        _code_building.value = code
    }

    fun setCapacity(count:Int){
        _check.value= count
    }

    fun setBuildingName(name:String){
        _name_building.value= name
    }

    fun ResetAll(){
        _code_building.value=""
        _name_building.value=""
    }
}