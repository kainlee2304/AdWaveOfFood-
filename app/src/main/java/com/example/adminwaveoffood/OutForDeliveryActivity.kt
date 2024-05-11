package com.example.adminwaveoffood

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.DeliveryAdapter
import com.example.adminwaveoffood.databinding.ActivityOutForDeliveryBinding
import com.example.adminwaveoffood.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding:ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private var listOfCompleteOrderList : ArrayList<OrderDetails> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            finish()
        }
        //truy xuat va hien thi don hang da  hoan thanh
        retrieveCompleteOrdorDetail()


    }

    private fun retrieveCompleteOrdorDetail() {
        // Khởi tạo Firebase Database
        database =FirebaseDatabase.getInstance()
        val completeOrderReference = database.reference.child("CompletedOrder").orderByChild("currentTime")

        completeOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Xóa danh sách trước khi điền dữ liệu mới
                listOfCompleteOrderList.clear()

                for (orderSnapshot in snapshot.children) {
                    val completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listOfCompleteOrderList.add(it)
                    }
                }

                // Đảo ngược danh sách để hiển thị thứ tự mới nhất trước tiên
                listOfCompleteOrderList.reverse()

                // Thiết lập dữ liệu vào RecyclerView sau khi đã thu thập tất cả dữ liệu từ Firebase
                setDataIntoRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình truy xuất dữ liệu từ Firebase
                Log.e(ContentValues.TAG, "Failed to retrieve complete order details: ${error.message}")
            }
        })

    }
    private fun setDataIntoRecyclerView() {
        // khai bao danh sách lưu giữ tên khách hàng và trạng thái thanh toán
        val customerName = mutableListOf<String>()
        val moneyStatus = mutableListOf<Boolean>()
        for(order in listOfCompleteOrderList){
            order.userName?.let {
                customerName.add(it)
            }
            moneyStatus.add(order.paymentReceived)
        }
        val adapter = DeliveryAdapter(customerName,moneyStatus)
        binding.deliveryRecyclerView.adapter = adapter
        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}