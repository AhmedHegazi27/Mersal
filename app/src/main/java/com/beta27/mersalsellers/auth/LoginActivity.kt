package com.beta27.mersalsellers.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.beta27.mersalsellers.R
import com.beta27.mersalsellers.databinding.ActivityLoginBinding
import com.beta27.mersalsellers.info.InfoActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        binding.loginBt.setOnClickListener {
            showLoading()
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.loginEtEmail.text.toString()).matches()) {
                binding.loginEtEmail.requestFocus()
                hideLoading()
                binding.loginEtEmail.error = "Enter Void Email"
                return@setOnClickListener
            } else if (binding.loginEtPassword.text.toString().length < 6) {
                binding.loginEtPassword.requestFocus()
                hideLoading()
                binding.loginEtPassword.error = "Enter Void Password"
                return@setOnClickListener
            } else {
                mAuth.signInWithEmailAndPassword(
                    binding.loginEtEmail.text.toString(),
                    binding.loginEtPassword.text.toString()
                )
                    .addOnSuccessListener {
                        hideLoading()
                        startActivity(
                            Intent(
                                this,
                                InfoActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                        finish()

                    }.addOnFailureListener {
                        hideLoading()
                        Snackbar.make(
                            CoordinatorLayout(this),
                            "${it.message}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        Toast.makeText(this, "${it.message}", Toast.LENGTH_LONG).show()

                    }
            }
        }
        binding.ftBtn.setOnClickListener { showForgetDialog() }


    }

    private fun showForgetDialog() {
        val dialog = MaterialDialog(this)
            .customView(R.layout.dialog_forget_layout).cornerRadius(15f)
        dialog.findViewById<Button>(R.id.dialog_send_bt).setOnClickListener {
            showLoading()
            if (!Patterns.EMAIL_ADDRESS.matcher(dialog.findViewById<EditText>(R.id.dialog_et_email).text.toString())
                    .matches()
            ) {
                dialog.findViewById<EditText>(R.id.dialog_et_email).error = "Enter Void Email"
                dialog.findViewById<EditText>(R.id.dialog_et_email).requestFocus()
                return@setOnClickListener
            } else {
                mAuth.sendPasswordResetEmail(dialog.findViewById<EditText>(R.id.dialog_et_email).text.toString())
                    .addOnFailureListener {
                        hideLoading()
                        Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener {
                        hideLoading()
                        Toast.makeText(this, "Check Your Email", Toast.LENGTH_SHORT).show()

                    }
            }

            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.loginBt.isEnabled = false
    }

    private fun hideLoading() {
        binding.loginBt.isEnabled = true
        binding.progressBar.visibility = View.INVISIBLE
    }
}