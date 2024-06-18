package com.example.adminwaveoffood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminwaveoffood.databinding.ItemItemBinding
import com.example.adminwaveoffood.model.AllMenu
import com.google.firebase.database.DatabaseReference


class MenuItemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    databaseReference: DatabaseReference,
    private val onDeleteClickListener:(position:Int)->Unit// Listener để xử lý sự kiện khi người dùng click vào nút xóa
) : RecyclerView.Adapter<MenuItemAdapter.AddItemViewHolder>() {
    private val itemQuantities = IntArray(menuList.size) { 1 }// Mảng lưu số lượng của từng món ăn

    // Tạo view holder cho mỗi item trong RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder{
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddItemViewHolder(binding)
    }

    // Gắn dữ liệu vào view holder tại vị trí được chỉ định
    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    // Trả về số lượng item trong danh sách
    override fun getItemCount(): Int = menuList.size

    // Lớp view holder cho mỗi item trong RecyclerView
    inner class AddItemViewHolder(private val binding: ItemItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Gắn dữ liệu vào view holder
        fun bind(position: Int) {
            binding.apply {
                // Lấy món ăn tại vị trí hiện tại
                val quantity:Int = itemQuantities[position]
                val menuItem:AllMenu = menuList[position]
                val uriString:String? = menuItem.foodImage
                val uri = Uri.parse(uriString)
                // Đặt tên và giá của món ăn
                foodNameTextView.text = menuItem.foodName
                foodPriceView.text =  menuItem.foodPrice

                // Sử dụng Glide để load ảnh từ đường dẫn (URI)
                Glide.with(context).load(uri).into(foodImageView)

                // Đặt số lượng và xử lý sự kiện cho nút tăng giảm số lượng
                quantityTextView.text = quantity.toString()

                minusbutton.setOnClickListener {
                    deceaseQuantity(position)
                }
                plusebutton.setOnClickListener {
                    inceaseQuantity(position)
                }

                // Xử lý sự kiện khi người dùng click vào nút xóa
                deleteButton.setOnClickListener {
                   onDeleteClickListener(position)
                }
            }
        }
        // Phương thức tăng số lượng của món ăn tại vị trí được chỉ định
        private fun inceaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                binding.quantityTextView.text = itemQuantities[position].toString()
            }
        }

        // Phương thức giảm số lượng của món ăn tại vị trí được chỉ định
        private fun deceaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                binding.quantityTextView.text = itemQuantities[position].toString()
            }
        }
        // Phương thức xóa món ăn tại vị trí được chỉ định khỏi menuList
        private fun deleteItem(position: Int) {
            menuList.removeAt(position)
            menuList.removeAt(position)
            menuList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, menuList.size)
        }

    }
}
