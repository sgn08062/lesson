package com.example.testmlkitocr

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION = android.Manifest.permission.READ_MEDIA_IMAGES

    private lateinit var btnAddImage: Button
    private lateinit var btnProcessImage: Button
    private lateinit var ivImage: ImageView
    private lateinit var tvImageText: TextView

    private lateinit var readImageText: ReadImageText
    private lateinit var imageCropper: ImageCropper

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageCropper.startCropActivity(it) // 선택된 이미지를 크롭 액티비티로 전달
        }
    }

    private fun processCroppedImage(uri: Uri) {
        ivImage.setImageDrawable(null)
        ivImage.setImageURI(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnAddImage = findViewById(R.id.btnAdd)
        btnProcessImage = findViewById(R.id.btnProp)
        ivImage = findViewById(R.id.ivSource)
        tvImageText = findViewById(R.id.tvResult)

        readImageText = ReadImageText()

        imageCropper = ImageCropper(
            this,
            onCropComplete = { uri ->
                processCroppedImage(uri) // 성공 시 크롭된 이미지 표시
            },
            onCropFailed = { error -> // 실패 시 이미지 크롭 실패 메시지
                error?.printStackTrace()
                Toast.makeText(this, "이미지 크롭 실패: ${error?.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        )

        btnAddImage.setOnClickListener {
            imagePicker.launch("image/*") // 이미지 선택
        }

        btnProcessImage.setOnClickListener {
            val bitmap = (ivImage.drawable as? BitmapDrawable)?.bitmap
            bitmap?.let {
                lifecycleScope.launch {
                    readImageText.processImage(it) { result ->
                        tvImageText.text = result // OCR 결과 표시
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val permissionCheck = ContextCompat.checkSelfPermission(applicationContext, STORAGE_PERMISSION)

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, STORAGE_PERMISSION)) {
                ActivityCompat.requestPermissions(this, arrayOf(STORAGE_PERMISSION), 0)
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
        } else {
            readImageText = ReadImageText()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        readImageText.close()
    }
}
