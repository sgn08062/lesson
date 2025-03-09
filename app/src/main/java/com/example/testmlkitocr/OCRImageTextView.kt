package com.example.testmlkitocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class OCRImageTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var image: Bitmap? = null
    var ocrResults: List<OCRResult> = emptyList()
    var overlayTextSize: Float = 40f

    private val textPaint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
        textSize = overlayTextSize
    }

    /**
     * image : 배경 이미지 (크롭한 이미지)
     * ocrResults : OCR 결과 리스트 (텍스트와 좌표)
     * textSize : (옵션) 텍스트 사이즈를 별도로 지정할 수 있음
     */
    fun setData(image: Bitmap, ocrResults: List<OCRResult>, textSize: Float? = null) {
        this.image = image
        this.ocrResults = ocrResults
        textSize?.let {
            overlayTextSize = it
            textPaint.textSize = it
        }
        invalidate() // 뷰 갱신
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 배경 이미지 그리기
        image?.let { bmp ->
            canvas.drawBitmap(bmp, 0f, 0f, null)
        }
        // OCR 결과 텍스트를 좌표에 맞게 그리기
        for (result in ocrResults) {
            canvas.drawText(result.text, result.boundingBox.left.toFloat(), result.boundingBox.top.toFloat(), textPaint)
        }
    }
}
