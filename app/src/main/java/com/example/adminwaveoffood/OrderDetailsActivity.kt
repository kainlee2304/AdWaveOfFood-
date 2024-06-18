package com.example.adminwaveoffood

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.OrderDetailsAdapter
import com.example.adminwaveoffood.databinding.ActivityOrderDetailsBinding
import com.example.adminwaveoffood.model.OrderDetails

class OrderDetailsActivity : AppCompatActivity() {

    private val binding: ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }
    // Khai báo các biến và view binding cho Activity
    private var userName : String?= null
    private var address : String?= null
    private var phoneNumber : String?=null
    private var totalPrice: String?= null
    private var foodNames: ArrayList<String> = arrayListOf()
    private var foodImages: ArrayList<String> = arrayListOf()
    private var foodQuantity:ArrayList<Int> = arrayListOf()
    private var foodPrices: ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener {
            finish()
        }
        // Lấy dữ liệu từ Intent gửi từ Activity trước
        getDataFromIntent()
    }
    // Hàm lấy dữ liệu từ Intent
    private fun getDataFromIntent() {
        // Lấy đối tượng OrderDetails từ Intent
        val receivedOrderDetails = intent.getSerializableExtra("UserOrderDetails") as OrderDetails
        receivedOrderDetails?.let { orderDetails ->
            // Gán các giá trị từ đối tượng OrderDetails vào các biến trong Activity
            userName = receivedOrderDetails.userName
            foodNames = receivedOrderDetails. foodNames as ArrayList<String>
            foodImages = receivedOrderDetails.foodImages as ArrayList<String>
            foodQuantity = receivedOrderDetails.foodQuantiies as ArrayList<Int>
            address = receivedOrderDetails.address
            phoneNumber = receivedOrderDetails.phoneNumber
            foodPrices = receivedOrderDetails.foodPrices as ArrayList<String>
            totalPrice = receivedOrderDetails.totalPrice
            // Gọi các hàm để hiển thị thông tin người dùng và thiết lập Adapter cho RecyclerView
            setUserDetail()
            setAdapter()
        }
    }
    // Hàm hiển thị thông tin người dùng lên giao diện
    private fun setUserDetail() {
        binding.name.text = userName
        binding.address.text = address
        binding.phone.text = phoneNumber
        binding.totalPay.text = totalPrice
    }
    // Hàm thiết lập Adapter cho RecyclerView hiển thị chi tiết đơn hàng
    private fun setAdapter() {
        binding.OrderDetailRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = OrderDetailsAdapter(this, foodNames, foodImages, foodQuantity, foodPrices)
        binding.OrderDetailRecyclerView.adapter = adapter
    }
}