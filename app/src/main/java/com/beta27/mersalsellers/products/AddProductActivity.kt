package com.beta27.mersalsellers.products

import android.app.Activity
import android.content.Intent
import android.icu.lang.UCharacter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import com.beta27.mersalsellers.R
import com.beta27.mersalsellers.databinding.ActivityAddProductBinding

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var product: Products
    private var hasCategory: Boolean = false
    private lateinit var category: String
    private val REQUEST_IMAGE_GET = 150
    private lateinit var fullPhotoUri: Uri
    private var hasImage: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_product)
        binding.categoryTv.setOnClickListener { getCategory() }
        product = Products()
        binding.discountSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                binding.discountPriceEt.visibility = View.VISIBLE
                binding.discountNoteEt.visibility = View.VISIBLE
                product.discount = true
            } else {
                binding.discountPriceEt.visibility = View.GONE
                binding.discountNoteEt.visibility = View.GONE
                product.discount = false
            }
        }
        binding.productImage.setOnClickListener { getImage() }
        binding.saveFab.setOnClickListener {
            if (hasCategory) {
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getCategory() {
        lateinit var dialog: AlertDialog

        val builder = AlertDialog.Builder(this)
        val array = arrayOf("Pizza", "Sandwich", "Meals", "Drinks", "Grocery", "Vegetables")
        builder.setTitle("Choose a category.")
        builder.setSingleChoiceItems(array, -1, { _, which ->
            category = array[which]
            hasCategory = true
            binding.categoryTv.text = category
            dialog.dismiss()
        })
        dialog = builder.create()
        dialog.show()
    }

    private fun getImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            fullPhotoUri = data?.data!!
            hasImage = true

            // Do work with photo saved at fullPhotoUri
            fullPhotoUri.let {
                binding.productImage.setImageURI(it)
            }

        }
    }

}