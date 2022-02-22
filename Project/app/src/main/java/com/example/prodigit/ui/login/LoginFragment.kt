package com.example.prodigit.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.prodigit.ApplicationActivity
import com.example.prodigit.DatabaseManager
import com.example.prodigit.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //For Google Login
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("89549313858-tdt72trcf3fa7e27rq9fsvglct8cdq7a.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }!!


        auth=Firebase.auth

        database= FirebaseDatabase.getInstance()
        reference = database.getReference("users")

        val view = inflater.inflate(R.layout.fragment_login, container, false)


        //Listeners for all the fields in the layout
        view.findViewById<Button>(R.id.login_button).setOnClickListener{goToApplication(view)}
        view.findViewById<ImageButton>(R.id.showPassword).setOnClickListener{showPass(view)}
        view.findViewById<ImageButton>(R.id.hidePassword).setOnClickListener{hidePass(view)}
        view.findViewById<Button>(R.id.button_go_signup).setOnClickListener{goToNextScreen()}
        view.findViewById<ImageButton>(R.id.imageButton).setOnClickListener { GoogleLogin() }
        view.findViewById<TextView>(R.id.google_login).setOnClickListener { GoogleLogin() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun showPass(view: View){
        val eye_show = view.findViewById<ImageButton>(R.id.showPassword)
        eye_show.setVisibility(View.INVISIBLE)
        val eye_hide = view.findViewById<ImageButton>(R.id.hidePassword)
        eye_hide.setVisibility(View.VISIBLE)
        view.findViewById<EditText>(R.id.passwordInput).transformationMethod= HideReturnsTransformationMethod.getInstance()
    }

    fun hidePass(view: View){
        val eye_show = view.findViewById<ImageButton>(R.id.showPassword)
        eye_show.setVisibility(View.VISIBLE)
        val eye_hide = view.findViewById<ImageButton>(R.id.hidePassword)
        eye_hide.setVisibility(View.INVISIBLE)
        view.findViewById<EditText>(R.id.passwordInput).transformationMethod= PasswordTransformationMethod.getInstance()

    }

    //Google Login
    private fun GoogleLogin(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 9001)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("GOOGLE LOGIN", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("GOOGLE LOGIN", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        activity?.let {
            auth.signInWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("GOOGLE", "signInWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user)
                        //Go to MAIN APPLICATION
                        val database_class = DatabaseManager()
                        database_class.addNewUser()
                        val intent = Intent(activity, ApplicationActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("GOOGLE", "signInWithCredential:failure", task.exception)
                        updateUI(null)
                    }
                }
        }
    }


    //Login with email and password
    private fun goToApplication(view: View){
        val email_input = view.findViewById<EditText>(R.id.emailAddressInput)
        val password_input = view.findViewById<EditText>(R.id.passwordInput)
        if(email_input.text.toString().isEmpty()){
            Log.i("LOGIN","ERROR")
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
        Log.i("LOGIN","TOAPPLICATION")
        activity?.let {
            auth.signInWithEmailAndPassword(email_input.text.toString(), password_input.text.toString())
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("LOGIN", "signInWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                        //Go to MAIN APPLICATION
                        val intent = Intent(activity, ApplicationActivity::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("LOGIN", "signInWithEmail:failure", task.exception)
                        val toast = Toast.makeText(
                            context, "Authentication Failed: Check username and password",
                            Toast.LENGTH_SHORT
                        )
                        toast.view?.setBackgroundResource(R.drawable.toast_style)
                        toast.show()
                        updateUI(null)
                    }
                }
        }
    }

    private fun reload(){

    }
    private fun updateUI(user: FirebaseUser?){

    }

    fun goToNextScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
    }
}