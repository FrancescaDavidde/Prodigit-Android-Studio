package com.example.prodigit.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.prodigit.ApplicationActivity
import com.example.prodigit.DatabaseManager
import com.example.prodigit.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        //To move into gotoApplication function ???
        database = FirebaseDatabase.getInstance() //Link to the root
        reference = database.getReference("users") //Link to users subtree


        //Listeners for all the elements in the layout
        view.findViewById<Button>(R.id.signup_button).setOnClickListener{goToApplication(view)}
        view.findViewById<ImageButton>(R.id.showPasswordInsert).setOnClickListener{showPass(view)}
        view.findViewById<ImageButton>(R.id.hidePasswordInsert).setOnClickListener{hidePass(view)}
        view.findViewById<ImageButton>(R.id.showPasswordConfirm).setOnClickListener{showPassConf(view)}
        view.findViewById<ImageButton>(R.id.hidePasswordConfirm).setOnClickListener{hidePassConf(view)}
        view.findViewById<Button>(R.id.button_go_login).setOnClickListener{goToNextScreen()}

        // Initialize Firebase Auth
        auth = Firebase.auth

        return view
    }

    fun showPass(view: View){
        val eye_show = view.findViewById<ImageButton>(R.id.showPasswordInsert)
        eye_show.setVisibility(View.INVISIBLE)
        val eye_hide = view.findViewById<ImageButton>(R.id.hidePasswordInsert)
        eye_hide.setVisibility(View.VISIBLE)
        view.findViewById<EditText>(R.id.passwordInputSignUp).transformationMethod= HideReturnsTransformationMethod.getInstance()
    }

    fun hidePass(view: View){
        val eye_show = view.findViewById<ImageButton>(R.id.showPasswordInsert)
        eye_show.setVisibility(View.VISIBLE)
        val eye_hide = view.findViewById<ImageButton>(R.id.hidePasswordInsert)
        eye_hide.setVisibility(View.INVISIBLE)
        view.findViewById<EditText>(R.id.passwordInputSignUp).transformationMethod= PasswordTransformationMethod.getInstance()

    }

    //Confirm
    fun showPassConf(view: View){
        val eye_show = view.findViewById<ImageButton>(R.id.showPasswordConfirm)
        eye_show.setVisibility(View.INVISIBLE)
        val eye_hide = view.findViewById<ImageButton>(R.id.hidePasswordConfirm)
        eye_hide.setVisibility(View.VISIBLE)
        view.findViewById<EditText>(R.id.passwordConfirmInputSignUp).transformationMethod= HideReturnsTransformationMethod.getInstance()
    }

    fun hidePassConf(view: View){
        val eye_show = view.findViewById<ImageButton>(R.id.showPasswordConfirm)
        eye_show.setVisibility(View.VISIBLE)
        val eye_hide = view.findViewById<ImageButton>(R.id.hidePasswordConfirm)
        eye_hide.setVisibility(View.INVISIBLE)
        view.findViewById<EditText>(R.id.passwordConfirmInputSignUp).transformationMethod= PasswordTransformationMethod.getInstance()

    }

    fun goToApplication(view: View){
        val email_input = view.findViewById<EditText>(R.id.emailAddressInputSignUp)
        val password_input = view.findViewById<EditText>(R.id.passwordInputSignUp)
        val password_confirm = view.findViewById<EditText>(R.id.passwordConfirmInputSignUp)
        if(email_input.text.toString().isEmpty()){
            Log.i("HERE","ERROR")
            email_input.error="Please insert an email"
            email_input.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email_input.text.toString()).matches()){
            email_input.error = "Please insert a valid email"
            email_input.requestFocus()
            return
        }
        if(password_input.text.toString().isEmpty()){
            password_input.error = "Please insert a password"
            password_input.requestFocus()
            return
        }
        if(password_confirm.text.toString().isEmpty() || password_confirm.text.toString()!=password_input.text.toString()){
            password_confirm.error = "You entered 2 different passwords"
            password_confirm.requestFocus()
            return
        }
        Log.i("HERE","TOAPPLICATION")

        //Everything is OK
        activity?.let { it ->
            auth.createUserWithEmailAndPassword(email_input.text.toString(), password_input.text.toString())
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("CREATE", "createUserWithEmail:success")
                        val uid = auth.currentUser
                        val database_class = DatabaseManager()
                        database_class.addNewUser()
                        val intent = Intent(activity, ApplicationActivity::class.java)
                        startActivity(intent)
                        //updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("CREATE", "createUserWithEmail:failure", task.exception)
                        val toast=Toast.makeText(context, "Error during the creation of the account",
                            Toast.LENGTH_SHORT)
                        toast.view?.setBackgroundResource(R.drawable.toast_style)
                        toast.show()
                        updateUI(null)
                    }
                }
        }
    }

    fun goToNextScreen() {
        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
    }

    fun updateUI(currentUser: FirebaseUser?){

    }
}