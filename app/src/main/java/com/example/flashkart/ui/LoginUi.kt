package com.example.flashkart.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

@Composable
fun LoginUi(flashViewModel: FlashViewModel){
    val context = LocalContext.current
    val otp by flashViewModel.otp.collectAsState()
    val verificationId by flashViewModel.verificationId.collectAsState()
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//            signInWithPhoneAuthCredential(credential, context, flashViewModel)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("Login", "Verification failed: ${e.message}")
            Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Log.d("Login", "onCodeSent: verificationId = $verificationId")
            flashViewModel.setVerificationId(verificationId)
            Toast.makeText(context, "OTP Sent", Toast.LENGTH_SHORT).show()
            flashViewModel.resetTimer()
            flashViewModel.runTimer()
        }

    }

    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Account Logo",
                modifier = Modifier
                    .padding(top = 50.dp, bottom = 10.dp)
                    .size(100.dp)
            )
            if (verificationId.isEmpty()) {
                NumberScreen(flashViewModel = flashViewModel, callbacks = callbacks)
            } else {
                OTPScreen(otp = otp, flashViewModel = flashViewModel, callbacks = callbacks)
            }
        }
        if (verificationId.isNotEmpty()) {
            IconButton(onClick = {
                flashViewModel.setVerificationId("")
                flashViewModel.setOTP("")
            }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")

            }
        }
    }
}
