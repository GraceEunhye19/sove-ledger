package com.example.sovereignledger.security

import com.google.mlkit.vision.face.Face
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

//this is where the math is to check the prob of eyes open

//this class analyze the camera frame to detect live
//class LivenessAnalyzer(
//    private val onResult: (LivenessResult) -> Unit //take livenessresult and update the UI
//): ImageAnalysis.Analyzer //from camera core
//{
//
//    //ml kit: landmarks(eyes) and classification(open/close)
//    private val options = FaceDetectorOptions.Builder()
//        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST) //do it fast
//        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL) //find eyes nose and mouth
//        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) //eye-open or closed
//        .build()
//
//    //track of user has their eyes open before closing aka blinking
//    private val detector = FaceDetection.getClient(options)
//
//    private var eyesWereOpen = false //why this
//    private var blinkCount = 0
//    private val requiredBlinks = 2
//
//    //analyze image data from the camera
//    @OptIn(ExperimentalGetImage::class)
//    override fun analyze(imageProxy: ImageProxy){
//        val mediaImg = imageProxy.image  //get the raw image first
//        if (mediaImg != null ){ //ml can only process inputimage not raw images (image or image proxy). rotate is to fix orientation
//            val image = InputImage.fromMediaImage(mediaImg, imageProxy.imageInfo.rotationDegrees)
//
//            detector.process(image)
//                .addOnSuccessListener { faces ->
//                if (faces.isEmpty()){
//                    onResult(LivenessResult.Searching)
//                    eyesWereOpen = false
//                } else {
//                    val face = faces[0] //main face
//                    val leftEye = face.leftEyeOpenProbability ?: 0f //the numbers are probs, 0-1
//                    val rightEye = face.rightEyeOpenProbability?: 0f
//
//                    if (leftEye> 0.8f && rightEye> 0.8f){eyesWereOpen = true} //detect open eyes
//                    else if(eyesWereOpen && leftEye <0.2f && rightEye < 0.8f){ //blinking
//                        blinkCount++
//                        eyesWereOpen = false //reset to avoid counting one blink multiple times
//
//                        if (blinkCount >= requiredBlinks){onResult(LivenessResult.Sucess)} else {onResult(
//                            LivenessResult.BlinkDetected)
//                        }
//
//                    }
//                }
//                }
//                .addOnCompleteListener { imageProxy.close() }  //if completed close frame
//        } else {imageProxy.close()}  //no face close frame
//    }
//}
//the brain

interface LivenessCallback { //bridge between the brain and the ui
    fun onFaceDetected(face: Face)
    fun onBlinkDetected()
    fun onLivenessVerified()
    fun onError(message: String) //e.g camera fail
}

class LivenessAnalyzer(private val callback: LivenessCallback) : ImageAnalysis.Analyzer {


    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) //only two modes anyway
        .build()

    private val detector = FaceDetection.getClient(options)

    private var isBlinking = false //prevents counting 1 blink more than once
    private var blinkCount = 0
    private val requiredBlinks = 2 //a glitch can look like a blink

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image //get the raw image first
        if (mediaImage != null) { //if image is valid
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            //feeds image to face detector
            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        val face = faces[0] //first face found
                        callback.onFaceDetected(face)
                        checkBlinking(face)
                    }
                }
                .addOnFailureListener { e ->
                    callback.onError(e.message ?: "Unknown error")
                }
                .addOnCompleteListener { //when done
                    imageProxy.close() //so camera x can send a new frame if not the app will freeze
                }
        } else {
            imageProxy.close() //if no image close anyway
        }
    }
    private fun checkBlinking(face: Face) {
        val leftEyeOpenProb = face.leftEyeOpenProbability ?: 1.0f //default eye is open
        val rightEyeOpenProb = face.rightEyeOpenProbability ?: 1.0f

        //botheyes closed
        if (leftEyeOpenProb < 0.2f && rightEyeOpenProb < 0.2f) {
            if (!isBlinking) {
                isBlinking = true
            }
        } else if (leftEyeOpenProb > 0.8f && rightEyeOpenProb > 0.8f) {
            if (isBlinking) {
                isBlinking = false
                blinkCount++
                callback.onBlinkDetected()

                if (blinkCount >= requiredBlinks) {
                    callback.onLivenessVerified()
                }
            }
        }
    }
}