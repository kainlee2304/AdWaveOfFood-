package com.example.adminwaveoffood

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.example.adminwaveoffood.databinding.ActivityAddItemBinding
import com.example.adminwaveoffood.model.AllMenu

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class AddItemActivity : AppCompatActivity() {
    //Food item Details
    private  lateinit var foodName: String
    private  lateinit var foodPrice: String
    private  lateinit var foodDescription: String
    private  lateinit var foodIngredient: String
    private  var foodImageUri: Uri? = null
    //Firebase
    private  lateinit var auth:FirebaseAuth
    private  lateinit var database: FirebaseDatabase

    private val binding:ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //initialize Firebase
        auth = FirebaseAuth.getInstance()
        // Intialize Firebase database instance
        database = FirebaseDatabase.getInstance()

        binding.AddItemButton.setOnClickListener{
            // Get Data from Filed
            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.foodPrice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredient = binding.ingredint.text.toString().trim()

            if (!foodName.isBlank() || foodPrice.isBlank() || foodDescription .isBlank() || foodIngredient.isBlank()){
                    uploadData()
                Toast.makeText(this, "Item add Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this, "Fill all the details", Toast.LENGTH_SHORT).show()
            }
        }
        binding.selectImage.setOnClickListener {
            pickImage.launch("image/*")
        }


        binding.backButton.setOnClickListener{
            finish()
        }
    }

    private fun uploadData() {

        //get a reference to the menu node in the database
        val menuRef:DatabaseReference = database.getReference("menu")
        //Genrate a unique key for the new menu item
        val  newItemKey: String? = menuRef.push().key

        if (foodImageUri != null){
            val storageRef:StorageReference = FirebaseStorage.getInstance().reference
            val imageRef:StorageReference = storageRef.child("menu_images/${newItemKey}.ipg")
            val uploadTask: UploadTask = imageRef.putFile(foodImageUri!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    //
                    // Create a new menu item
                    val newItem = AllMenu(
                        newItemKey,
                        foodName = foodName,
                        foodPrice = foodPrice,
                        foodDescription = foodDescription,
                        foodIngredient = foodIngredient,
                        foodImage = downloadUrl.toString(),
                    )
                    newItemKey?.let { key ->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this, "data upload successfully", Toast.LENGTH_SHORT)
                                .show()
                        }
                            .addOnFailureListener {
                                Toast.makeText(this, "data upload failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }
            } .addOnFailureListener {
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
            } else {
                            Toast.makeText(this, "Please slect an image", Toast.LENGTH_SHORT).show()
            }

            }



    val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){
            uri ->
        if (uri != null) {
        binding.selectedImage.setImageURI(uri)
            foodImageUri = uri
    }
    }


}