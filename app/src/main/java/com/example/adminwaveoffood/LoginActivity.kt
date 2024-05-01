package com.example.adminwaveoffood

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.adminwaveoffood.databinding.ActivityLoginBinding
import com.example.adminwaveoffood.model.UserModel
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private var userName : String ?= null
    private var nameOfRestaurant : String ?= null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var googleSignInClient:GoogleSignInClient




    private val binding:ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val googleSignUpOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        auth = Firebase.auth

        database = Firebase.database.reference

        //khai bao dang nhap google
        googleSignInClient = GoogleSignIn.getClient(this, googleSignUpOptions)


        binding.loginButton.setOnClickListener{
        // get text from edit text

            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()
            if (email.isBlank()|| password.isBlank()){
                Toast.makeText(this,"Hãy nhập đầy đủ thông tin!",Toast.LENGTH_SHORT).show()
            }else{
                createUserAccount(email, password)
            }
        }
        binding.googleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

            binding.dontHaveButton.setOnClickListener{
                val intent = Intent(this,SignUpActivity::class.java)
                startActivity(intent)
        }
    }

    private fun createUserAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
        if (task.isSuccessful){
            val user = auth.currentUser
            Toast.makeText(this,"Login Successfull",Toast.LENGTH_SHORT).show()
            updateUi(user)
        }else{
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
                if (task.isSuccessful){
                    val user = auth.currentUser
                    Toast.makeText(this,"Create User & Login Successfull",Toast.LENGTH_SHORT).show()
                    saveUserData()
                    updateUi(user)

                }else{
                    Toast.makeText(this,"Đăng nhập thất bại",Toast.LENGTH_SHORT).show()
                    Log.d("Account","Đăng nhập thất bại,Hãy tạo tài khoản mới",task.exception)

                }
            }
        }



        }
    }

    private fun saveUserData() {
        // get text from edit text

        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()

        val user = UserModel(userName,nameOfRestaurant,email,password)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            database.child("user").child(it).setValue(user)
        }

    }


    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if (result.resultCode == RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account : GoogleSignInAccount = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken ,null)
                auth.signInWithCredential(credential).addOnCompleteListener{authTask ->
                    if (authTask.isSuccessful){
                        //đăng nhập thành công với google
                        Toast.makeText(this,"Đăng nhập google thành công ", Toast.LENGTH_SHORT).show()
                        updateUi(user=null)
                    }else{
                        Toast.makeText(this,"Đăng nhập google thất bại ", Toast.LENGTH_SHORT).show()

                    }

                }
            }
            else{
                Toast.makeText(this,"Đăng nhập google thất bại ", Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currencyUser = auth.currentUser
        if (currencyUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
    private fun updateUi(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

