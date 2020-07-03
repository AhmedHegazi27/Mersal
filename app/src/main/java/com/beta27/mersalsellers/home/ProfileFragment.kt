package com.beta27.mersalsellers.home

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.beta27.mersalsellers.R
import com.beta27.mersalsellers.auth.LoginActivity
import com.beta27.mersalsellers.databinding.FragmentProfileBinding
import com.beta27.mersalsellers.info.InfoActivity
import com.beta27.mersalsellers.info.Seller
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private val sellersCollectionRef = Firebase.firestore.collection("sellers")
    private lateinit var binding: FragmentProfileBinding
    private val email = Firebase.auth.currentUser?.email
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.edit.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    InfoActivity::class.java
                )
            )
        }
        binding.logoutBtn.setOnClickListener {

            Firebase.auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            activity?.finish()


        }
        getProfileData()
        binding.active.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        sellersCollectionRef.document(Firebase.auth.currentUser?.uid.toString())
                            .update("active", true).await()
                    } catch (e: Exception) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                requireContext(), e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        sellersCollectionRef.document(Firebase.auth.currentUser?.uid.toString())
                            .update("active", false).await()
                    } catch (e: Exception) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Toast.makeText(
                                requireContext(), e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
        return binding.root

    }

    private fun getProfileData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot = Firebase.firestore.collection("sellers")
                    .document(Firebase.auth.currentUser?.uid!!).get().await()
                if (querySnapshot.data != null) {
                    val seller: Seller = querySnapshot.toObject<Seller>()!!
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.email.text = email
                        binding.shopName.text = seller.shopName
                        binding.name.text = seller.name
                        Glide.with(this@ProfileFragment).load(seller.imageUrl)
                            .into(binding.profileImg)

                        binding.active.isChecked = seller.active

                    }
                }

            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        this@ProfileFragment.context,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }


}