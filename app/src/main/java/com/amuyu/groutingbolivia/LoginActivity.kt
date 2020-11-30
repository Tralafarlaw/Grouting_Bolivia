package com.amuyu.groutingbolivia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login_buttton.setOnClickListener {
            login_buttton.isEnabled = false
            login_mail.text.also {
                if(it.isNullOrEmpty() ){
                    Toast.makeText(this, "Porfavor ingrese su correo electronico", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val mail = it.toString()
                login_pass.text.also {et ->
                if(et.isNullOrEmpty()){
                    Toast.makeText(this, "Porfavor ingrese su correo electronico", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                    val pass = et.toString()
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mail, pass).addOnSuccessListener {
                        Intent(this, MainActivity::class.java).also {pp->
                            Toast.makeText(this, "Bienvenido ${it.user?.displayName}", Toast.LENGTH_SHORT).show()
                            startActivity(pp)
                            finish()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Eroor al iniciar sesion porfavor intente de nuevo en unos momentos", Toast.LENGTH_SHORT).show()
                        login_buttton.isEnabled = true
                    }.addOnFailureListener {
                        login_buttton.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null){
            Intent(this, MainActivity::class.java).also {pp->
                startActivity(pp)
                finish()
            }
        }
    }
}