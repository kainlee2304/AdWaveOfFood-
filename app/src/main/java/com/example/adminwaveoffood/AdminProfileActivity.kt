package com.example.adminwaveoffood

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.adminwaveoffood.databinding.ActivityAdminProfileBinding
import com.example.adminwaveoffood.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {
    // Sử dụng view binding để liên kết giao diện người dùng
    private val binding:ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }
    // Khai báo các biến cho Firebase
    private lateinit var database:FirebaseDatabase
    private lateinit var auth:FirebaseAuth
    private lateinit var adminReference:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
// Khởi tạo Firebase Auth và Database
        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        adminReference=database.reference.child("user")

        // Xử lý sự kiện khi nút quay lại được nhấn
        binding.backButton.setOnClickListener{
            finish()
        }
        // Xử lý sự kiện khi nút lưu thông tin được nhấn
        binding.saveInfoButton.setOnClickListener {
            updateUserData()
        }

// Vô hiệu hóa các trường nhập liệu và nút lưu thông tin ban đầu
        binding.name.isEnabled = false
        binding.address.isEnabled = false
        binding.email.isEnabled = false
        binding.phone.isEnabled = false
        binding.password.isEnabled = false
        binding.saveInfoButton.isEnabled=false
        // Xử lý sự kiện khi nút chỉnh sửa được nhấn
        var isEnable = false
        binding.editButton.setOnClickListener {
            isEnable = ! isEnable
            binding.name.isEnabled = isEnable
            binding.address.isEnabled = isEnable
            binding.email.isEnabled = isEnable
            binding.phone.isEnabled = isEnable
            binding.password.isEnabled = isEnable

            if (isEnable){
                binding.name.requestFocus()
            }
            binding.saveInfoButton.isEnabled=isEnable
        }
// Lấy dữ liệu người dùng từ Firebase
        retrieveUserData()

    }


    // Hàm lấy dữ liệu người dùng từ Firebase
    private fun retrieveUserData() {
        var currentUserUid=auth.currentUser?.uid
        if (currentUserUid!=null){
            val userReference=adminReference.child(currentUserUid)
            userReference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        var ownerName=snapshot.child("name").getValue()
                        var address=snapshot.child("address").getValue()
                        var email=snapshot.child("email").getValue()
                        var phone=snapshot.child("phone").getValue()
                        var password=snapshot.child("password").getValue()

                        setDataToTextView(ownerName,address,email,phone,password)
                    }
                }
                // Xử lý khi có lỗi xảy ra
                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }
    // Hàm đặt dữ liệu vào các trường TextView
    private fun setDataToTextView(ownerName: Any?, address: Any?, email: Any?, phone: Any?, password: Any?) {
        binding.name.setText(ownerName.toString())
        binding.address.setText(address.toString())
        binding.email.setText(email.toString())
        binding.phone.setText(phone.toString())
        binding.password.setText(password.toString())

    }
    // Hàm cập nhật dữ liệu người dùng
    private fun updateUserData() {
        var updateName=binding.name.text.toString()
        var updateEmail=binding.email.text.toString()
        var updatePassword=binding.password.text.toString()
        var updatePhone=binding.phone.text.toString()
        var updateAddress=binding.address.text.toString()

        val currentUserUid=auth.currentUser?.uid
        if (currentUserUid!=null) {
            val userReference = adminReference.child(currentUserUid)
            userReference.child("name").setValue(updateName)
            userReference.child("email").setValue(updateEmail)
            userReference.child("password").setValue(updatePassword)
            userReference.child("phone").setValue(updatePhone)
            userReference.child("address").setValue(updateAddress)

            Toast.makeText(this, "Profile Updated Successfully \uD83D\uDE04", Toast.LENGTH_SHORT)
                .show()
            //update email and password
            auth.currentUser?.verifyBeforeUpdateEmail(updateEmail)
            auth.currentUser?.updatePassword(updatePassword)
        }else{
            Toast.makeText(this, "Profile Updated Fail \uD83D\uDE13", Toast.LENGTH_SHORT).show()
        }


    }
}