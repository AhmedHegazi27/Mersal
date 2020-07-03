package com.beta27.mersalsellers.info

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.beta27.mersalsellers.R
import com.beta27.mersalsellers.databinding.ActivityInfoBinding
import com.beta27.mersalsellers.home.HomeActivity
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*


class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    private lateinit var locationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var location: Location
    private var hasLocation: Boolean = false
    private lateinit var mAuth: FirebaseAuth
    private var hasImage: Boolean = false
    private val sellersCollectionRef = Firebase.firestore.collection("sellers")
    private val sellersStorageRef = Firebase.storage.reference
    private lateinit var fullPhotoUri: Uri
    private lateinit var seller: Seller

    private val LOCATION_PERMISSION: Int = 100
    private val REQUEST_IMAGE_GET: Int = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_info)
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability?) {
                super.onLocationAvailability(p0)
                if (p0?.isLocationAvailable()!!) {
                }
            }

            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)
            }
        }
        val manager =
            getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation()
        } else {
            statusCheck()
        }
        binding.shopImg.setOnClickListener {
            getImage()
        }
        mAuth = FirebaseAuth.getInstance()
        binding.button.setOnClickListener {
            if (binding.infoNameEt.text.isEmpty()) {
                binding.infoNameEt.error = "Enter Your Name"
                binding.infoNameEt.requestFocus()
                return@setOnClickListener
            } else if (binding.infoShopNameEt.text.isEmpty()) {
                binding.infoShopNameEt.error = "Enter Shop Name"
                binding.infoShopNameEt.requestFocus()
                return@setOnClickListener
            } else if (!Patterns.PHONE.matcher(binding.infoPhoneEt.text).matches()) {
                binding.infoPhoneEt.error = "Enter Your Phone Number"
                binding.infoPhoneEt.requestFocus()
                return@setOnClickListener

            } else if (hasLocation == false) {
                getLocation()
                Toast.makeText(this, "we need to get your location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else if (hasImage == false) {
                Toast.makeText(this, "Please Select an image", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            } else if (!this::location.isInitialized) {
                return@setOnClickListener
            } else {
                binding.progressBar.visibility = View.VISIBLE
                saveData(
                    mAuth.currentUser?.uid!!, binding.infoNameEt.text.toString()
                    , binding.infoShopNameEt.text.toString()
                    , binding.infoPhoneEt.text.toString()
                    , location.longitude, location.latitude
                )

            }
        }

    }


    private fun showMap(geoLocation: Location) {
        // Creates an Intent that will load a map of San Francisco
        val gmmIntentUri = Uri.parse("geo:${geoLocation.latitude},${geoLocation.longitude}?z=20")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK) {
            fullPhotoUri = data?.data!!
            hasImage = true

            // Do work with photo saved at fullPhotoUri
            fullPhotoUri.let {
                binding.shopImg.setImageURI(it)
            }

        }
    }

    private fun getImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager
                .PERMISSION_GRANTED
        ) {
            locationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                this.mainLooper
            )
            locationProviderClient.lastLocation.addOnSuccessListener { currentLocation ->
                try {
                    location = currentLocation
                    hasLocation = true
                } catch (e: Exception) {
                    statusCheck()
                    hasLocation = false
                }
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION
                    ), LOCATION_PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        getLocation()
    }

    private fun statusCheck() {
        val manager =
            getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
            .setNegativeButton("No",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun saveData(
        uid: String,
        name: String,
        shopName: String,
        phone: String,
        longitude: Double,
        latitude: Double
    ) = CoroutineScope(Dispatchers.IO).launch {
        try {
            fullPhotoUri.let {
                val ref =
                    sellersStorageRef.child("images/profile/${mAuth.currentUser?.uid}").putFile(it)
                        .await()
                val a = ref.storage.downloadUrl.await()
                val seller = Seller(uid, name, shopName, phone, longitude, latitude, a.toString())
                sellersCollectionRef.document(uid).set(seller).await()
                val infoSharedPref = getSharedPreferences("infoShared", Context.MODE_PRIVATE)
                val editor = infoSharedPref.edit()
                val pass = editor.putBoolean("pass", true).commit()
                if (pass) {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.progressBar.visibility = View.INVISIBLE
                    }

                    startActivity(Intent(this@InfoActivity, HomeActivity::class.java))
                    finish()
                }
            }
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBar.visibility = View.INVISIBLE
                Toast.makeText(this@InfoActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}