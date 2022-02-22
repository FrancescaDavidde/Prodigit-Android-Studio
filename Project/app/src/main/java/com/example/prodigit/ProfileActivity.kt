package com.example.prodigit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileActivity : AppCompatActivity(R.layout.myprofile) {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<TextView>(R.id.email_view).text=Firebase.auth.currentUser?.email.toString()

        //Listener to remove the account
        findViewById<Button>(R.id.button_remove_account).setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("REMOVING ACCOUNT")
            builder.setMessage("Are you sure you want to remove this account?")

            builder.setPositiveButton("Yes") { _, _ ->

                val user = Firebase.auth.currentUser!!
                val database_class = DatabaseManager()
                database_class.removeAccount()

                user.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //Remove account and go to the Login
                            Log.d("REMOVE_USER", "User account deleted.")
                            startActivity(Intent(this,MainActivity::class.java))
                        }
                    }

            }
            builder.setNegativeButton("No"){_,_ ->
                Log.d("REMOVE_USER", "Not remove the account.")
            }
            builder.show()


        }

        findViewById<Button>(R.id.button_changepass).setOnClickListener {
            val user = Firebase.auth.currentUser

            if(user!=null){
                user.email?.let {it1->
                    Firebase.auth.sendPasswordResetEmail(it1)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Log.d("CHEK RESET PASS"," Reset Succeed")
                            }
                        }

                }
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("CHANGE PASSWORD")
            builder.setMessage("An email has been sent to your address")
            builder.setPositiveButton("Ok") { _, _ ->
                //SignOut and go to the Login
                Firebase.auth.signOut()
                startActivity(Intent(this, MainActivity::class.java))
            }
            builder.show()
        }

        //Listener to logout
        findViewById<Button>(R.id.button_logout).setOnClickListener {
            val user = Firebase.auth.currentUser

            //For dedug
            if (user != null) {
                Log.d("CHECK_USER","USER IS LOGGED")
            } else {
                Log.d("CHECK_USER"," NO-USER IS LOGGED")
            }

            //SignOut and go to the Login
            Firebase.auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            android.R.id.home -> {
                val intent = Intent(this, ApplicationActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}