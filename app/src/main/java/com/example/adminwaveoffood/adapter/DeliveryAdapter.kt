package com.example.adminwaveoffood.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminwaveoffood.databinding.ActivityAllItemBinding
import com.example.adminwaveoffood.databinding.DeliveryItemBinding

class DeliveryAdapter(
    //Khởi tạo adapter với customersname và monystatus
    private val customerNames:MutableList<String>,
    private val moneyStatus:MutableList<Boolean>,
    //Adapter  gán cho RecyclerView để bắt đầu quá trình hiển thị danh sách các mục.
    ):RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {


//RecyclerView gọi phương thức onCreateViewHolder để tạo ra các ViewHolder cần thiết để hiển thị các mục trên màn hình.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = DeliveryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DeliveryViewHolder(binding)
    }
//holder.bind(position) gọi phương thức bind của DeliveryViewHolder để cập nhật giao diện với dữ liệu tại vị trí position.
    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(position)
    }
    //Phương thức này trả về số lượng mục trong danh sách, dựa trên kích thước của customerNames.
    override fun getItemCount(): Int =customerNames.size
//bind :dữ liệu từ danh sách (tên khách hàng và trạng thái thanh toán) được gắn kết vào các view tương ứng.
    inner class DeliveryViewHolder(private val binding: DeliveryItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int){
            binding.apply {
                customerName.text = customerNames[position]
                if(moneyStatus[position]== true){
                    statusMoney.text = "Received"
                } else{
                    statusMoney.text = "notReceived"
                }
                //colorMap để gán màu sắc tương ứng cho statusMoney và statusColor (xanh lá cây cho "Received", đỏ cho "notReceived").
                val colorMap = mapOf(true to Color.GREEN,false to Color.RED)
                //thay đổi màu chữ.
                statusMoney.setTextColor(colorMap[moneyStatus[position]]?:Color.BLACK)
                //thay đổi màu nền.
                statusColor.backgroundTintList = ColorStateList.valueOf(colorMap[moneyStatus[position]]?:Color.BLACK)
            }
        }

    }
}