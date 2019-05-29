package sobaya.app.mlface

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.android.gms.vision.face.FaceDetector.ACCURATE_MODE
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        face(BitmapFactory.decodeResource(resources, R.drawable.test1))
    }

    private fun face(bmp: Bitmap) {

        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST).build()
        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setRotation(FirebaseVisionImageMetadata.ROTATION_0)
            .build()
        detector.detectInImage(FirebaseVisionImage.fromBitmap(bmp))
            .addOnSuccessListener {
                it.forEach { face ->
                    val left = face.leftEyeOpenProbability
                    val right = face.rightEyeOpenProbability
                    val nose = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)
                }
            }
            .addOnFailureListener {
                it.toString()
            }
    }
}
