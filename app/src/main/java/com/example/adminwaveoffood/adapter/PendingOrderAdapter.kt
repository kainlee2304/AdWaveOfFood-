package com.example.adminwaveoffood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminwaveoffood.databinding.PendingOdersItemBinding


class PendingOrderAdapter(
    private val context: Context,
    private val customerNames:MutableList<String>,
    private val quantity:MutableList<String>,
    private val foodImage: MutableList<String>,
    private val itemClicked: OnItemClicked,
):RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    interface OnItemClicked{
        fun onItemClickListener(position: Int)
        fun onItemAcceptClickListener(position: Int)
        fun onItemDispatchClickListener(position: Int)


    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PendingOrderViewHolder {
       val binding = PendingOdersItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PendingOrderViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }
    // Trả về số lượng item trong danh sách
    override fun getItemCount(): Int = customerNames.size
    // Lớp ViewHolder cho mỗi item trong RecyclerView
    inner class PendingOrderViewHolder(private val binding: PendingOdersItemBinding):RecyclerView.ViewHolder(binding.root) {
        // Biến để theo dõi trạng thái đơn hàng đã được chấp nhận hay chưa
        private var isAccepted = false

        // Gắn dữ liệu vào ViewHolder
        fun bind(position: Int){
            binding.apply {
                customerName.text = customerNames[position]
                pendingOredarQuantity.text=quantity[position]
                var uriString = foodImage[position]
                var uri=Uri.parse(uriString)
                Glide.with(context).load(uri).into(orderfoodImage)

                // Xử lý sự kiện click cho nút chấp nhận/đã gửi
                orderedAcceptButton.apply {
                    if(!isAccepted){
                        text="Accept"// Nếu chưa chấp nhận, hiển thị nút Accept
                    }else{
                        text="Dispatch"// Nếu đã chấp nhận, hiển thị nút Dispatch
                    }
                    setOnClickListener{
                        if (!isAccepted){
                            text="Dispatch"
                            isAccepted = true
                            showToast("Order is accepted")
                            itemClicked.onItemAcceptClickListener(position)
                        }else{
                            // Xóa đơn hàng khỏi danh sách và thông báo rằng đã gửi hàng
                            customerNames.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            showToast("Order is dispatched ")
                            itemClicked.onItemDispatchClickListener(position)
                        }

                    }
                }
                // Xử lý sự kiện click vào item trong RecyclerView
                itemView.setOnClickListener{
                    itemClicked.onItemClickListener(position)
                }

            }

        }
        // Phương thức hiển thị toast message
        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

}