package com.aziz.sabbela

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setupEdgeToEdge(findViewById(R.id.rootLayout), lightNavBar = false)

        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                FirebaseDatabase.getInstance("https://sabbela-default-rtdb.europe-west1.firebasedatabase.app")
                    .reference.child("admins").child(user.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val dest = if (snapshot.exists()) AdminActivity::class.java
                            else HomeActivity::class.java
                            startActivity(Intent(this@SplashActivity, dest))
                            finish()
                        }
                        override fun onCancelled(error: DatabaseError) {
                            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                            finish()
                        }
                    })
            }
        }, 2500)
    }
}
