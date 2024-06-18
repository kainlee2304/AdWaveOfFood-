package com.example.adminwaveoffood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminwaveoffood.databinding.OrderDetailItemBinding

class OrderDetailsAdapter(
    private var context: Context,
    private var foodNames: ArrayList<String>,
    private var foodImages: ArrayList<String>,
    private var foodQuantitys:ArrayList<Int>,
    private var foodPrices: ArrayList<String>
) : RecyclerView.Adapter<OrderDetailsAdapter. OrderDetailsViewHolder>(){

    // Tạo view holder cho mỗi item trong RecyclerView
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailsAdapter.OrderDetailsViewHolder {
        // Inflate layout của item
        val binding =
            OrderDetailItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderDetailsViewHolder(binding)
    }
    // Gắn dữ liệu vào view holder tại vị trí được chỉ định
    override fun onBindViewHolder(holder: OrderDetailsAdapter.OrderDetailsViewHolder, position: Int) {
        holder.bind(position)

    }

    // Trả về số lượng item trong danh sách
    override fun getItemCount(): Int =foodNames.size

    // Lớp view holder cho mỗi item trong RecyclerView
    inner class OrderDetailsViewHolder(private val binding: OrderDetailItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        // Gắn dữ liệu vào view holder
        fun bind(position: Int) {
            binding.apply {
                foodName.text = foodNames[position]
                foodQuantity.text = foodQuantitys[position].toString()
                // Load ảnh món ăn từ đường dẫn (URI) sử dụng Glide
                val uriString = foodImages[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(foodImage)
                foodPrice.text = foodPrices[position]
            }
        }


    }

}