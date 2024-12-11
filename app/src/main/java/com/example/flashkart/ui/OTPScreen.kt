package com.example.flashkart.ui

import android.app.Activity
import android.content.Context
import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@Composable
fun OTPScreen(otp: String, flashViewModel: FlashViewModel,callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks) {
    val context = LocalContext.current
    val verificationId by flashViewModel.verificationId.collectAsState()
    val ticks by flashViewModel.ticks.collectAsState()
    val phoneNumber by flashViewModel.phoneNumber.collectAsState()

    OTPTextBox(otp = otp, flashViewModel = flashViewModel)

    Button(
        onClick = {
            if (otp.isEmpty()) {
                Toast.makeText(context, "OTP is empty", Toast.LENGTH_SHORT).show()
            }
            else {
                val credential = PhoneAuthProvider.getCredential(verificationId, otp)
                signInWithPhoneAuthCredential(credential, context, flashViewModel)
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Verify", fontSize = 24.sp)
    }
    Text(text =
    if (ticks==0L){
        "Resend OTP"
    }else{
        "Resend OTP (${DateUtils.formatElapsedTime(ticks)})"
    }, modifier = Modifier.clickable{val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber("+91${phoneNumber}") // Phone number to verify
        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
        .setActivity(context as Activity) // Activity (for callback binding)
        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        .build()
        PhoneAuthProvider.verifyPhoneNumber(options) })
}

@Composable
fun OTPTextBox(otp:String,
               flashViewModel: FlashViewModel){
    BasicTextField(value = otp, onValueChange ={
        flashViewModel.setOTP(it)
    }, modifier = Modifier.fillMaxWidth(), singleLine = true ){
        Row(horizontalArrangement = Arrangement.Center){
            repeat(6){index->val number=when{
                index>=otp.length->""
                else->otp[index].toString()
            }
                Column(modifier = Modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = number, fontSize = 32.sp)
                    Box(modifier = Modifier
                        .width(40.dp)
                        .height(2.dp)
                        .background(Color.LightGray)){

                    }
                }
            }
        }
    }
}
private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential,context: Context,flashViewModel: FlashViewModel) {
//    val auth = FirebaseAuth.getInstance()
    auth.signInWithCredential(credential)
        .addOnCompleteListener(context as Activity ) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
//                Log.d(TAG, "signInWithCredential:success")
                Toast.makeText(context, "Sign in success", Toast.LENGTH_SHORT).show()
                val user = task.result?.user
                if (user != null) {
                    flashViewModel.setUser(user)
                }
            } else {
                // Sign in failed, display a message and update the UI
//                Log.w(TAG, "signInWithCredential:failure", task.exception)
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(context, "The verification code entered was invalid", Toast.LENGTH_SHORT).show()
                    // The verification code entered was invalid
                }
                // Update UI
            }
        }
}