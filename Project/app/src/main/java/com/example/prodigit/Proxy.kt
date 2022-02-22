package com.example.prodigit

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class Proxy(val howOften: Long, val callback: (String, String) -> Unit,val callback2: (String) -> Unit, var BuildingCode:String,var mylat:String,var mylon:String) {

    inner class MyTimer:TimerTask() {
         override fun run() {
             val reply = doPoll()

             build_lat=reply["lat"].toString()
             build_lon=reply["lon"].toString()
             callback(reply["lat"].toString(),reply["lon"].toString())

             val reply2 = doDistance()
             //callback(41.78595402865499.toString(), 12.383269210138517.toString())
             Log.d("PRINT_REPLY ",reply2.getJSONArray("routes").getJSONObject(0).getDouble("distance").toString())
             //Log.d("PRINT_REPLY ",reply2.getJSONObject("routes").getDouble("distance").toString())
             if(mylat==41.78979990820697f.toString()){
                 callback2("...")
             }else{
                 callback2(reply2.getJSONArray("routes").getJSONObject(0).getDouble("distance").toString())
             }
             //Log.i("POLL",reply["lat"].toString())
         }

    }
    lateinit var timer : Timer
    lateinit var build_lat:String
    lateinit var build_lon:String

    fun start(){
        timer=Timer("poll", true)
        timer.scheduleAtFixedRate(MyTimer(),0,howOften)
    }
    fun pause(){
        timer.cancel()
    }
    fun doPoll(): JSONObject {
        val name =
            "https://ProdigitProject.pythonanywhere.com/"+BuildingCode
        Log.d("BUILDING_CODE",BuildingCode)
        val url = URL(name)
        val conn = url.openConnection() as HttpsURLConnection
        try {
            //Log.i("POLL", "ok")
            conn.run {
                requestMethod = "GET"
                val reply = JSONObject(InputStreamReader(inputStream).readText())
                return reply
            }
        } catch (e: Exception) {
            Log.v("POLL", e.toString())
            return JSONObject()
        }
    }

    fun doDistance():JSONObject{
        //val name = "http://router.project-osrm.org/route/v1/foot/12.4446,41.8694;12.513492793308037,41.9087254844291?overview=false"
        val name = "https://router.project-osrm.org/route/v1/foot/"+mylon+","+mylat+";"+build_lon+","+build_lat+"?overview=false"
        Log.d("DISTANCE","http://router.project-osrm.org/route/v1/foot/"+mylon+","+mylat+";"+build_lon+","+build_lat+"?overview=false")
        val url = URL(name)
        val conn = url.openConnection() as HttpsURLConnection
        try {
            //Log.i("POLL", "ok")
            conn.run {
                requestMethod = "GET"
                val reply = JSONObject(InputStreamReader(inputStream).readText())
                return reply
            }
        } catch (e: Exception) {
            Log.v("POLL", e.toString())
            return JSONObject()
        }
    }
}

