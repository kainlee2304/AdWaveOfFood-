package com.example.adminwaveoffood

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.PendingOrderAdapter
import com.example.adminwaveoffood.databinding.ActivityPendingOrderBinding
import com.example.adminwaveoffood.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrderActivity : AppCompatActivity(),PendingOrderAdapter.OnItemClicked {
    // Khai báo các biến và view binding cho Activity
    private lateinit var binding: ActivityPendingOrderBinding
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private var listOfOderItem: ArrayList<OrderDetails> = arrayListOf()
    private lateinit var databaseOrderDetails: DatabaseReference
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Khởi tạo Firebase và thiết lập reference đến "OrderDetails"
        database = FirebaseDatabase.getInstance()
        databaseOrderDetails = database.reference.child("OrderDetails")
        // Lấy dữ liệu các đơn hàng chờ xử lý từ Firebase
        getOrderDetails()

        binding.backButton.setOnClickListener {
            finish()
        }


    }
    // Hàm lấy dữ liệu các đơn hàng chờ xử lý từ Firebase
    private fun getOrderDetails() {
        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    // Đọc dữ liệu từ snapshot và chuyển đổi thành đối tượng OrderDetails
                    val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
                    orderDetails?.let {
                        listOfOderItem.add(it) // Thêm đối tượng OrderDetails vào danh sách
                    }
                }
                // Sau khi lấy dữ liệu, gọi hàm để chuẩn bị dữ liệu cho RecyclerView
                addDataToListForRecyclerView()

            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi đọc dữ liệu từ Firebase thất bại
                Log.e("Firebase", "Failed to read value.", error.toException())
            }

        })
    }
    // Hàm chuẩn bị dữ liệu từ danh sách OrderDetails cho RecyclerView
    private fun addDataToListForRecyclerView() {
        for (orderItem in listOfOderItem) {
            // Thêm các thông tin cần thiết vào danh sách để hiển thị
            //add data
            orderItem.userName?.let { listOfName.add(it) }
            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach {
                listOfImageFirstFoodOrder.add(it)
            }
        }
        // Gọi hàm để thiết lập Adapter cho RecyclerView

        setAdapter()
    }
    // Hàm thiết lập Adapter cho RecyclerView để hiển thị danh sách đơn hàng chờ xử lý
    private fun setAdapter() {
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter =
            PendingOrderAdapter(this, listOfName, listOfTotalPrice, listOfImageFirstFoodOrder, this)
        binding.pendingOrderRecyclerView.adapter = adapter
    }
    // Xử lý sự kiện khi item trong RecyclerView được click (chuyển đến chi tiết đơn hàng)
    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderDetail = listOfOderItem[position]
        intent.putExtra("UserOrderDetails", userOrderDetail)
        startActivity(intent)
    }
    // Xử lý sự kiện khi nút "Accept" trên item trong RecyclerView được click
    override fun onItemAcceptClickListener(position: Int) {
        //xu ly viec xac nhan mat hang va update du lieu
        val childItemPushKey: String? = listOfOderItem[position].itemPushKey
        val clickItemOrderReference = childItemPushKey?.let {
            database.reference.child("OrderDetails").child(it)
        }
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
        updateOrderAccpetStatus(position)
    }
    // Xử lý sự kiện khi nút "Dispatch" trên item trong RecyclerView được click
    override fun onItemDispatchClickListener(position: Int) {
        //xu ly viec gui di mat hang va update du lieu
        val dispatchItemPushkey = listOfOderItem[position].itemPushKey
        val dispatchItemOrderReference =
            database.reference.child("CompletedOrder").child(dispatchItemPushkey!!)
        dispatchItemOrderReference.setValue(listOfOderItem[position])
            .addOnSuccessListener {
                deleteThisItemFromOrderDetails(dispatchItemPushkey)
            }
    }
    // Hàm xóa đơn hàng đã được gửi đi từ "OrderDetails"
    private fun deleteThisItemFromOrderDetails(dispatchItemPushkey: String){
        val orderDetailsItemReference = database.reference.child("OrderDetails").child(dispatchItemPushkey)
        orderDetailsItemReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "OrderIs is Dispatched ", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "OrderIs is not Dispatched ", Toast.LENGTH_SHORT).show()
            }
    }
    // Hàm cập nhật trạng thái xác nhận đơn hàng trong "BuyHistory" và "OrderDetails"
    private fun updateOrderAccpetStatus(position: Int) {
        //cap nhat xac nhan don hang trong  buyhistory và orderdetails
        val userIdOfClickedItem = listOfOderItem[position].userId
        val pushKeyOfClickedItem = listOfOderItem[position].itemPushKey
        val buyHistoryReference = database.reference.child("user").child(userIdOfClickedItem!!).child("BuyHistory").child(pushKeyOfClickedItem!!)
        buyHistoryReference.child("orderAccepted").setValue(true)
        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)

    }
}