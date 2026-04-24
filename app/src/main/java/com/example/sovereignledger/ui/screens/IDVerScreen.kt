package com.example.sovereignledger.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner

import com.example.sovereignledger.ui.theme.AppShapes
import com.example.sovereignledger.ui.theme.BackgroundGray
import com.example.sovereignledger.ui.theme.CardOutline
import com.example.sovereignledger.ui.theme.SovereignBlue
import com.example.sovereignledger.ui.theme.SovereignNavy
import com.example.sovereignledger.ui.theme.SurfaceWhite
import com.example.sovereignledger.ui.theme.TextSecondary
import com.example.sovereignledger.ui.theme.WarningOrange
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.sovereignledger.security.LivenessAnalyzer
import com.example.sovereignledger.security.LivenessCallback
import com.example.sovereignledger.ui.components.AppButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.face.Face
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun IDVerScreen(
    onVerificationSuccess: () -> Unit,
    onCancel: () -> Unit
){
    //val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    //state where camera has started
    var isVerifying by remember { mutableStateOf(false) }
    var blinkCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray) // F7F8FA
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFDDE6F8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Face, contentDescription = null, tint = SovereignNavy)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Identity Verification",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SovereignNavy
        )

        Text(
            text = "We need to perform a quick liveness check",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .background(Color(0xFFDDE6F8), AppShapes.pill)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, null, modifier = Modifier.size(14.dp), tint = SovereignBlue)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Center your face in the frame", fontSize = 12.sp, color = SovereignNavy)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(260.dp)
                .border(8.dp, SovereignBlue, CircleShape)
                .padding(8.dp)
                .clip(CircleShape)
                .background(Color(0xFFDDE6F8)),
            contentAlignment = Alignment.Center
        ) {
            //camera starts and permission is granted
            if (isVerifying && cameraPermissionState.status.isGranted) {
                CameraPreview(
                    onBlinkDetected = { blinkCount++ },
                    onSuccess = onVerificationSuccess
                )
            } else {
                // Placeholder before start
                Icon(Icons.Default.Videocam, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            //if camera up starts state
            if (isVerifying) {
                // Yellow "Blink Twice" Box
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .clip(AppShapes.cardItem)
                        .background(Color(0xFFEDDB8A))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Blink Twice If you're safe",
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color.White, AppShapes.pill)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Lock, null, modifier = Modifier.size(12.dp), tint = SovereignNavy)
                Spacer(modifier = Modifier.width(6.dp))
                Text("End-to-end encrypted", fontSize = 11.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- BUTTONS ---

        AppButton(
            text = "Start Verification →",
            onClick = {
                if (!cameraPermissionState.status.isGranted) {
                    cameraPermissionState.launchPermissionRequest()
                } else {
                    isVerifying = true
                }
            }
        )

        TextButton(
            onClick = {
                isVerifying = false
                onCancel()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Cancel", color = SovereignBlue, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CameraPreview(
    onBlinkDetected: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    //val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView( //jetpack doesnt have it own native camera component
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get() //camera life cycle
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                //creates an instance of the analyzer
                val analyzer = LivenessAnalyzer(object : LivenessCallback {
                    override fun onFaceDetected(face: Face) {}
                    override fun onBlinkDetected() { onBlinkDetected() }
                    override fun onLivenessVerified() { onSuccess() }
                    override fun onError(message: String) {}
                })

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) //avoid lag is the ml kit is slow
                    .build()
                    .also {
                        //seperate thread to keep ui smooth
                        it.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_FRONT_CAMERA, //not back camera
                        preview, //allow users to see themselves
                        imageAnalysis //analyzer aka ml to see the user too
                    )
                } catch (e: Exception) { e.printStackTrace() }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

