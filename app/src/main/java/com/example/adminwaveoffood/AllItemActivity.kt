package com.example.adminwaveoffood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminwaveoffood.adapter.MenuItemAdapter
import com.example.adminwaveoffood.databinding.ActivityAllItemBinding
import com.example.adminwaveoffood.model.AllMenu
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AllItemActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var menuItems: ArrayList<AllMenu> = ArrayList()
    private val binding: ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Khởi tạo tham chiếu tới Firebase Database
        databaseReference = FirebaseDatabase.getInstance().reference
        // Gọi hàm lấy dữ liệu món ăn từ Firebase
        retrieveMenuItem()


        binding.backButton.setOnClickListener {
            finish()
        }

    }
    // Hàm lấy dữ liệu món ăn từ Firebase
    private fun retrieveMenuItem() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            // Hàm thiết lập adapter cho RecyclerView
            private fun setAdaper() {
                val adapter = MenuItemAdapter(this@AllItemActivity, menuItems, databaseReference){position->
                    deleteMenuItem(position)
                }
                binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this@AllItemActivity)
                binding.MenuRecyclerView.adapter = adapter
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()// Xóa danh sách món ăn hiện tại
// Duyệt qua các phần tử trong DataSnapshot và thêm vào danh sách món ăn
                for (foodSnapshot: DataSnapshot in snapshot.children) {
                    val menuItem: AllMenu? = foodSnapshot.getValue(AllMenu::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                // Thiết lập adapter cho RecyclerView
                setAdaper()
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
             Log.d("DatabaseError","Error:${error.message}")
            }
        })
    }
    // Hàm xóa món ăn khỏi Firebase và cập nhật RecyclerView
    private fun deleteMenuItem(position: Int) {
        val menuItemToDelete=menuItems[position]
        val menuItemKey=menuItemToDelete.key
        val foodMenuReference=database.reference.child("menu").child(menuItemKey!!)
        foodMenuReference.removeValue().addOnCompleteListener {
            task->
            if (task.isSuccessful){
                menuItems.removeAt(position)
                binding.MenuRecyclerView.adapter?.notifyItemRemoved(position)
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Item not delete", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
