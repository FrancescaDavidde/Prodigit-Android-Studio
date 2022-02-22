package com.example.prodigit

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.Paint.Align.*
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.opengl.ETC1.getWidth
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.withRotation
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.*


class NavigationActivity : AppCompatActivity() {

    val array=arrayOf("CU001","CU002","CU003","RM100","RM101","RM102","LT002")
    var BuildingCode="LT002"

    lateinit var myview : MyView
    lateinit var sensorManager : SensorManager
    lateinit var locationManager : LocationManager

    var mRotationVector = FloatArray(3)
    var mRotMatrix = FloatArray(9)
    var mOrientation = FloatArray(3)
    lateinit var compass : Bitmap
    lateinit var arroww : Bitmap


    val size = 1050
    val ArrowHeight = 500
    val ArrowWidth = 170
    var mylat=41.78979990820697f
    var mylon=12.385296632987663f

    var dis = "..."
    var check=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            check=1
            setContentView(R.layout.activity_navigation)
            Log.d("ERROR_VIEW","HERE")
            return
        }

        /*
        //Alert Dialog
        val builder = AlertDialog.Builder(this)
        val foregroundColorSpan = ForegroundColorSpan(Color.BLACK)
        val text="Choose Building:"
        val ssBuilder = SpannableStringBuilder(text)
        ssBuilder.setSpan(
            foregroundColorSpan,
            0,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.setTitle(ssBuilder)
        builder.setItems(array) { _, which ->
            // Get the dialog selected item
            BuildingCode = array[which]

            Log.d("SELECTED_BUILDING", BuildingCode)
        }
        val dialog=builder.create()
        val listView: ListView = dialog.listView
        listView.setDivider(ColorDrawable(Color.BLACK))
        listView.setDividerHeight(3)
        dialog.show()
         */

        compass = ResourcesCompat.getDrawable(resources,R.drawable.compass1,
            null)?.
        toBitmap(size,size)!!

        arroww = ResourcesCompat.getDrawable(resources,R.drawable.arroww,
            null)?.
        toBitmap(ArrowWidth,ArrowHeight)!!

        myview= MyView(this)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            myview,
            sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
            SensorManager.SENSOR_DELAY_NORMAL)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        supportActionBar?.setTitle("Navigation System")

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5f, MyView(this))
        setContentView(myview)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                val intent = Intent(this, ApplicationActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            /*findViewById<Button>(R.id.select_map).setOnClickListener {

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Building")
                builder.setMessage("Do you want to see the building on the map?")

                builder.setPositiveButton("Yes") { _, _ ->

                    startActivity(Intent(this,MainActivity::class.java))
                }


                builder.setNegativeButton("No"){_,_ ->

                }
                builder.show()
            }*/
            R.id.select_building -> {
                //Alert Dialog
                val builder = AlertDialog.Builder(this)
                val foregroundColorSpan = ForegroundColorSpan(Color.BLACK)
                val text="Choose Building:"
                val ssBuilder = SpannableStringBuilder(text)
                ssBuilder.setSpan(
                    foregroundColorSpan,
                    0,
                    text.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                builder.setTitle(ssBuilder)
                builder.setItems(array) { _, which ->
                    // Get the dialog selected item
                    dis="..."
                    BuildingCode = array[which]

                    Log.d("SELECTED_BUILDING", BuildingCode)
                }
                val dialog=builder.create()
                val listView: ListView = dialog.listView
                listView.setDivider(ColorDrawable(Color.BLACK))
                listView.setDividerHeight(3)
                dialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
   /* public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Listener to remove the account
        findViewById<Button>(R.id.button_remove_account).setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Building")
            builder.setMessage("Do you want to see the building on the map?")

            builder.setPositiveButton("Yes") { _, _ ->

                            startActivity(Intent(this,MainActivity::class.java))
                        }


            builder.setNegativeButton("No"){_,_ ->

            }
            builder.show()
        }
    }*/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        menuInflater.inflate(R.menu.action_bar_building, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        if(check==0)
            myview.start()


    }
    override fun onPause() {
        super.onPause()
        if(check==0)
            myview.pause()
    }


    inner class MyView(context: Context?) : View(context), SensorEventListener, LocationListener {

        //Local variables
        var lat = "0" //buddy latitude
        var lon = "0" //buddy longitude
        var yaw = 0f //my way
        var buddyyaw = 0f //buddy yaw

        //proxy to the server
        var proxy: Proxy

        //Painters
        val textPaint = Paint().apply {
            textSize = 60f
            color = Color.BLACK
            strokeWidth = 50f
        }
        val buddyPaint = Paint().apply {
            textSize = 60f
            color = Color.BLACK
            strokeWidth = 20f
        }

        //Called when a new data is available
        val callback = fun(latitude: String, longitude: String) {
            lat = latitude
            lon = longitude
            buddyyaw = 90 - angle(mylat, mylon, lat.toFloat(), lon.toFloat())
            invalidate()
        }

        val callback_distance = fun(distance:String){
            dis=distance
            Log.d("DISTANCE_NAV",dis)
            invalidate()
        }

        init {
            proxy = Proxy(1000, callback,callback_distance, BuildingCode,mylat.toString(),mylon.toString())

            Log.i("INFO", "BUDDY ANGLE: " + angle(42f, 22f, 32f, 22f).toString())
        }

        //New function not of the prof
        fun distanceMeters(): Double {
            val R = 6378.137 //Radius of Earth in KM
            var dLat = (mylat * Math.PI / 180) - (lat.toFloat() * Math.PI / 180)
            var dLon = (mylon * Math.PI / 180) - (lon.toFloat() * Math.PI / 180)
            var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(mylat * Math.PI / 180) * Math.cos(lat.toFloat() * Math.PI / 180) *
                    Math.sin(dLon / 2) * Math.sin(dLon / 2)
            var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            var d = R * c
            return d * 1000
        }



        //UI drawing
        override fun onDraw(cv: Canvas) {
            super.onDraw(cv)
            proxy.BuildingCode=BuildingCode
            proxy.mylat=mylat.toString()
            proxy.mylon=mylon.toString()
            with(cv) {
                val xPos: Int = cv.getWidth() / 2
                drawRGB(255, 255, 255)
                //drawText("MYLAT: " + mylat + " LON: " + mylon, 0f, 100f, textPaint)
                //drawText("BUDDYLAT:  " + lat + " LON: " + lon, 0f, 150f, textPaint)
                //drawText("MYYAW: "+yaw,0f,200f,textPaint)
                //drawText("BUDDYYAW: "+buddyyaw,0f,250f,textPaint)
                drawText("Destination: "+BuildingCode,xPos.toFloat(),200f,textPaint)

                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.CEILING

                if(dis=="..."){
                    drawText("Distance: " + dis, xPos.toFloat(), 350f, textPaint)
                }
                else if (dis.toFloat() < 1000) {
                    drawText("Distance: " + df.format(dis.toFloat()) + " m", xPos.toFloat(), 350f, textPaint)
                }
                else if (dis.toFloat() >= 1000){
                    drawText("Distance: " + df.format(dis.toFloat()/1000) + " km", xPos.toFloat(), 350f, textPaint)
                }



                textPaint.setTextAlign(CENTER)
                withRotation(-yaw,
                    cv.width/2f,
                    cv.height/2f) {
                    drawBitmap(compass,(width-size)/2f,(height-size)/2f,null)
                }
                withRotation(
                    -yaw + buddyyaw,
                    cv.width / 2f,
                    cv.height / 2f

                ) {
                    drawBitmap(arroww,(width-ArrowWidth)/2f,height/2f-ArrowHeight,null)
                }
                /*
                drawLine(
                    width / 2f,
                    height / 2f,
                    width / 2f,
                    height / 4f,
                    textPaint
                )
                 */

            }
        }

        //Lifecycle-aware data polling
        fun start() {
            proxy.start()
        }

        fun pause() {
            proxy.pause()
        }

        //Sensor handlers
        override fun onSensorChanged(event: SensorEvent) {
            mRotationVector = event.values.clone()
            SensorManager.getRotationMatrixFromVector(mRotMatrix, mRotationVector)
            SensorManager.getOrientation(mRotMatrix, mOrientation)
            yaw = mOrientation[0] * 180 / Math.PI.toFloat()
            invalidate()
        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            //TODO("Not yet implemented")
        }

        //Support function
        fun distance(lat1: Float, lon1: Float, lat2: Float, lon2: Float): Float {
            return acos(
                cos(lat1) * cos(lon1) * cos(lat2) * cos(lon2) +
                        cos(lat1) * sin(lon1) * cos(lat2) * sin(lon2) +
                        sin(lat1) * sin(lat2)
            )
        }

        fun angle(lat1: Float, lon1: Float, lat2: Float, lon2: Float): Float {
            val deltaLat = distance(lat1, lon1, lat2, lon1) * sign(lat2 - lat1)
            val deltaLon = distance(lat1, lon1, lat1, lon2) * sign(lon2 - lon1)
            val a = 180f * atan2(deltaLat, deltaLon) / Math.PI.toFloat()
            Log.i("INFO", "distLat: " + deltaLat + " distLon: " + deltaLon + " a: " + a)
            return a
        }

        override fun onLocationChanged(location: Location) {
            Log.d("LOCATION","Changed to "+ location.latitude.toString()+"|"+location.longitude.toString())
            mylat = location.latitude.toFloat()
            mylon = location.longitude.toFloat()
        }

    }



    /*
    override fun onLocationChanged(location: Location) {
        Log.d("LOCATION","Changed to "+ location.latitude.toString()+"|"+location.longitude.toString())
        mylat = location.latitude.toFloat()
        mylon = location.longitude.toFloat()
    }

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
     */
}