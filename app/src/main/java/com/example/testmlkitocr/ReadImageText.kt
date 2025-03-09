package com.example.testmlkitocr

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.vision.text.Text

// OCR 결과를 텍스트와 좌표로 저장할 데이터 클래스
data class OCRResult(val text: String, val boundingBox: Rect)

class ReadImageText {
    // 한국어 및 라틴 문자(영어) OCR을 위한 Recognizer
    private val recognizerKorean = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
    private val recognizerLatin = TextRecognition.getClient(TextRecognizerOptions.Builder().build())

    /**
     * OCR 결과를 OCRResult 리스트(텍스트와 좌표)로 반환하는 함수
     */
    fun processImageWithCoordinates(image: Bitmap, callback: (List<OCRResult>) -> Unit) {
        val inputImage = InputImage.fromBitmap(image, 0)
        recognizerKorean.process(inputImage)
            .addOnSuccessListener { result ->
                val ocrResults = parseOCRResult(result)
                if (ocrResults.isNotEmpty()) {
                    callback(ocrResults)
                } else {
                    processLatinTextWithCoordinates(inputImage, callback)
                }
            }
            .addOnFailureListener {
                processLatinTextWithCoordinates(inputImage, callback)
            }
    }

    private fun processLatinTextWithCoordinates(image: InputImage, callback: (List<OCRResult>) -> Unit) {
        recognizerLatin.process(image)
            .addOnSuccessListener { result ->
                val ocrResults = parseOCRResult(result)
                callback(ocrResults)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    // 텍스트 결과에서 각 라인의 텍스트와 bounding box를 추출
    private fun parseOCRResult(textResult: Text): List<OCRResult> {
        val results = mutableListOf<OCRResult>()
        for (block in textResult.textBlocks) {
            for (line in block.lines) {
                line.boundingBox?.let { box ->
                    results.add(OCRResult(line.text, box))
                }
            }
        }
        return results
    }

    // OCR이 끝난 후 메모리 해제
    fun close() {
        recognizerKorean.close()
        recognizerLatin.close()
    }
}
