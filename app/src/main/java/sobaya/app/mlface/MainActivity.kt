package sobaya.app.mlface

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import permissions.dispatcher.*
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    companion object {
        const val CAMERA_REQUEST_CODE = 100
    }

    var path: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        callCamera()
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.smile)
        val firebase = FirebaseVisionImage.fromBitmap(bmp)
        face(firebase)
    }

    @OnShowRationale(Manifest.permission_group.STORAGE)
    fun rationale() {
        Toast.makeText(this, "写真を保存させてくださいmm", Toast.LENGTH_SHORT).show()
    }

    @OnPermissionDenied(Manifest.permission_group.STORAGE)
    fun denied() {
        Toast.makeText(this, "権限をくださいmm", Toast.LENGTH_SHORT).show()
    }

    @OnNeverAskAgain(Manifest.permission_group.STORAGE)
    fun neverAskAgain() {
        Toast.makeText(this, "もうダメだぁ", Toast.LENGTH_SHORT).show()
    }

    //fixme I can not get the authority
    @NeedsPermission(Manifest.permission_group.STORAGE)
    fun callCamera() {

        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.JAPAN).format(Date())
        val fileName = "geed_${timeStamp}"
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "ULTRAMAN")

        if (!storageDir.exists()) {
            storageDir.mkdir()
        }
        val file = File.createTempFile(
            fileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )

        path = file.absolutePath
        val uri = FileProvider.getUriForFile(this, packageName, file)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }

        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put("_data", path)
            }
            contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            val inputStream = FileInputStream(File(path))
            val bmp = BitmapFactory.decodeStream(inputStream)
            val image = FirebaseVisionImage.fromBitmap(bmp)

            face(image)
        }
    }

    private fun face(image: FirebaseVisionImage) {

        val highAccuracyOpts = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
            .setContourMode(FirebaseVisionFaceDetectorOptions.NO_CONTOURS)
            .build()

        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(highAccuracyOpts)

        detector.detectInImage(image)
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "SUCCESS", Toast.LENGTH_SHORT).show()
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
            .addOnFailureListener {
                it.toString()
            }
            .addOnCompleteListener {
                it.toString()
            }
            .addOnCanceledListener {
                1.toString()
            }
    }
}
