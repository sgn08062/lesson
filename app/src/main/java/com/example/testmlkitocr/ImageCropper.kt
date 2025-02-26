package com.example.testmlkitocr

import android.content.Context
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.yalantis.ucrop.UCrop
import java.io.File

class ImageCropper(
    private val context: Context,
    private val onCropComplete: (Uri) -> Unit,
    private val onCropFailed: (Throwable?) -> Unit // 실패 시 처리 콜백 추가
) {
    private val cropActivityResult =
        (context as AppCompatActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val resultUri = UCrop.getOutput(result.data!!)
                if (resultUri != null) {
                    onCropComplete(resultUri) // 성공 시 크롭된 이미지 반환
                } else {
                    onCropFailed(NullPointerException("Cropped image URI is null")) // 실패 시 예외 전달
                }
            } else {
                val cropError = UCrop.getError(result.data!!)
                onCropFailed(cropError) // 실패 시 오류 전달
            }
        }

    fun startCropActivity(uri: Uri) {
        val destinationUri = Uri.fromFile(File(context.cacheDir, "croppedImage_${System.currentTimeMillis()}.jpg"))
        val options = UCrop.Options().apply {
            setFreeStyleCropEnabled(true)
            setCompressionQuality(100)
        }
        val cropIntent = UCrop.of(uri, destinationUri)
            .withOptions(options)
            //.withAspectRatio(1f, 1f)
            .getIntent(context)

        cropActivityResult.launch(cropIntent)
    }
}
