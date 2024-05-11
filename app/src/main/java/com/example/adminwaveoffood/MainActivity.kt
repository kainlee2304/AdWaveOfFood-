package com.example.adminwaveoffood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.adminwaveoffood.adapter.PendingOrderAdapter
import com.example.adminwaveoffood.databinding.ActivityMainBinding
import com.example.adminwaveoffood.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var database:FirebaseDatabase
    private lateinit var auth:FirebaseAuth
    private lateinit var completeOrderReference:DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.addMenu.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }

        binding.allItemMenu.setOnClickListener {
                val intent = Intent(this, AllItemActivity::class.java)
                startActivity(intent)
        }

        binding.outForDeliveryButton.setOnClickListener {
            val intent = Intent(this, OutForDeliveryActivity::class.java)
            startActivity(intent)
        }

        binding.profile.setOnClickListener {
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)
        }
        binding.createUser.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
        }
        binding.pendingOrderTextView.setOnClickListener {
            val intent = Intent(this, PendingOrderActivity::class.java)
            startActivity(intent)
        }
        binding.logOutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        pendingOrders()
        completedOrders()
        wholeTimeEaring()

    }

    private fun wholeTimeEaring() {
        var listTotalPay= mutableListOf<Int>()
        completeOrderReference=FirebaseDatabase.getInstance().reference.child("CompletedOrder")
        completeOrderReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               for (orderSnapshot in snapshot.children){
                   var completeOrder=orderSnapshot.getValue(OrderDetails::class.java)
                   completeOrder?.totalPrice?.replace("$","")?.toIntOrNull()?.let {
                       i-> listTotalPay.add(i)
                   }
               }
                binding.wholeTimeEaring.text=listTotalPay.sum().toString() + "$"

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun completedOrders() {
        var completedOrderdReference=database.reference.child("CompletedOrder")
        var completedOrderItemCount=0
        completedOrderdReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                completedOrderItemCount=snapshot.childrenCount.toInt()
                binding.completeOrders.text=completedOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun pendingOrders() {
        database=FirebaseDatabase.getInstance()
        var pendingOrderReference=database.reference.child("OrderDetails")
        var pendingOrderItemCount=0
        pendingOrderReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingOrderItemCount=snapshot.childrenCount.toInt()
                binding.pendingOrders.text=pendingOrderItemCount.toString()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}