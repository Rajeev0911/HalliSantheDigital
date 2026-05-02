package com.halliSanthe.app.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.halliSanthe.app.R
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var etPhone: EditText
    private lateinit var etOtp: EditText
    private lateinit var etName: EditText
    private lateinit var btnSendOtp: Button
    private lateinit var btnVerifyOtp: Button
    private lateinit var layoutOtp: LinearLayout
    private lateinit var layoutName: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvTitle: TextView
    private lateinit var tvSubtitle: TextView

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etPhone = findViewById(R.id.et_phone)
        etOtp = findViewById(R.id.et_otp)
        etName = findViewById(R.id.et_name)
        btnSendOtp = findViewById(R.id.btn_send_otp)
        btnVerifyOtp = findViewById(R.id.btn_verify_otp)
        layoutOtp = findViewById(R.id.layout_otp)
        layoutName = findViewById(R.id.layout_name)
        progressBar = findViewById(R.id.progress_bar)
        tvTitle = findViewById(R.id.tv_title)
        tvSubtitle = findViewById(R.id.tv_subtitle)

        btnSendOtp.setOnClickListener {
            sendOtp()
        }

        btnVerifyOtp.setOnClickListener {
            verifyOtp()
        }
    }

    private fun sendOtp() {
        val phone = etPhone.text.toString().trim()

        if (TextUtils.isEmpty(phone) || phone.length < 10) {
            etPhone.error = "Enter valid 10-digit phone number"
            return
        }

        showLoading(true)

        val fullPhone = "+91$phone"

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(fullPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    showLoading(false)
                    signInWithCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    showLoading(false)

                    Toast.makeText(
                        this@LoginActivity,
                        "OTP failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onCodeSent(
                    vId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    showLoading(false)

                    verificationId = vId

                    layoutOtp.visibility = View.VISIBLE
                    btnVerifyOtp.visibility = View.VISIBLE
                    btnSendOtp.text = "Resend OTP"

                    tvSubtitle.text = "OTP sent to +91$phone"

                    Toast.makeText(
                        this@LoginActivity,
                        "OTP sent!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtp() {
        val otp = etOtp.text.toString().trim()
        val name = etName.text.toString().trim()

        if (TextUtils.isEmpty(otp) || otp.length < 6) {
            etOtp.error = "Enter 6-digit OTP"
            return
        }

        if (TextUtils.isEmpty(name)) {
            etName.error = "Enter your name"
            return
        }

        if (verificationId == null) {
            Toast.makeText(this, "Please request OTP first", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)

        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->

                showLoading(false)

                if (task.isSuccessful && task.result != null) {

                    val uid = task.result.user?.uid ?: ""
                    val phone = task.result.user?.phoneNumber ?: ""

                    var name = etName.text.toString().trim()

                    if (name.isEmpty()) {
                        name = "Artisan"
                    }

                    saveUserToFirestore(uid, name, phone)

                } else {

                    Toast.makeText(
                        this,
                        "Wrong OTP. Try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun saveUserToFirestore(
        uid: String,
        name: String,
        phone: String
    ) {

        val user = hashMapOf<String, Any>(
            "uid" to uid,
            "name" to name,
            "phone" to phone
        )

        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {

                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->

                Toast.makeText(
                    this,
                    "Login failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showLoading(show: Boolean) {

        progressBar.visibility =
            if (show) View.VISIBLE else View.GONE

        btnSendOtp.isEnabled = !show
        btnVerifyOtp.isEnabled = !show
    }
}