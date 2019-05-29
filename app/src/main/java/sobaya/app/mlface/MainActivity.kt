package sobaya.app.mlface

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        face(BitmapFactory.decodeResource(resources, R.drawable.sleep))
    }

    private fun face(bmp: Bitmap) {

        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)   // 顔のパーツ位置
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)   // 目が開いてるかと笑顔判定
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS) // 輪郭
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE) // パフォーマンス
            .build()
        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)
        // ↓未使用になった？
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setRotation(FirebaseVisionImageMetadata.ROTATION_0)
            .build()

        detector.detectInImage(FirebaseVisionImage.fromBitmap(bmp))
            .addOnSuccessListener {
                it.forEach { face ->
                    // 左目開いてるか
                    val leftEyeOpen = face.leftEyeOpenProbability
                    // 右目開いてるか
                    val rightEyeOpen = face.rightEyeOpenProbability
                    // 笑ってるか
                    val smile = face.smilingProbability
                    // 顔の向き
                    val headEulerY = face.headEulerAngleY
                    val headEulerZ = face.headEulerAngleZ

                    val boundingBox = face.boundingBox // 顔の境界矩形

                    // 顔のパーツ位置情報
                    val leftEyePosition = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)
                    val rightEyePosition = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)
                    val nose = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)
                    val mouseLeft = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT)
                    val mouseRight = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT)
                    it.toString()
                }
            }
            .addOnFailureListener {}
    }
}
