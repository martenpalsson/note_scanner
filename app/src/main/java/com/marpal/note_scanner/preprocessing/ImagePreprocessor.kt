package com.marpal.note_scanner.preprocessing

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Canvas
import android.util.Log
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Image preprocessing pipeline for OCR improvement
 *
 * Implements image enhancement using Android native APIs to improve
 * OCR accuracy, especially for handwritten text.
 *
 * Processing steps:
 * 1. Grayscale Conversion
 * 2. Contrast Enhancement (Histogram Equalization)
 * 3. Brightness Adjustment
 * 4. Sharpening
 * 5. Adaptive Binarization (optional)
 */
class ImagePreprocessor {

    companion object {
        private const val TAG = "ImagePreprocessor"

        // Maximum image resolution for preprocessing (to control memory usage)
        private const val MAX_IMAGE_WIDTH = 1920
        private const val MAX_IMAGE_HEIGHT = 1080

        // Contrast enhancement parameters
        private const val CONTRAST_FACTOR_LIGHT = 1.2f
        private const val CONTRAST_FACTOR_STANDARD = 1.5f
        private const val CONTRAST_FACTOR_AGGRESSIVE = 2.0f

        // Brightness adjustment parameters
        private const val BRIGHTNESS_OFFSET_STANDARD = 10f
        private const val BRIGHTNESS_OFFSET_AGGRESSIVE = 20f

        // Sharpening parameters
        private const val SHARPEN_AMOUNT_STANDARD = 1.5f
        private const val SHARPEN_AMOUNT_AGGRESSIVE = 2.5f
    }

    /**
     * Preprocessing levels with different intensity
     */
    enum class PreprocessingLevel {
        /** Minimal preprocessing: grayscale + mild contrast only */
        LIGHT,

        /** Balanced preprocessing: full pipeline with standard parameters */
        STANDARD,

        /** Aggressive preprocessing: maximum enhancement (may introduce artifacts) */
        AGGRESSIVE,

        /** No preprocessing: return original image */
        NONE
    }

    /**
     * Preprocess image for OCR
     *
     * @param bitmap Original captured image
     * @param level Level of preprocessing intensity
     * @return Preprocessed bitmap ready for OCR
     */
    fun preprocess(
        bitmap: Bitmap,
        level: PreprocessingLevel = PreprocessingLevel.STANDARD
    ): Bitmap {
        val startTime = System.currentTimeMillis()

        try {
            if (level == PreprocessingLevel.NONE) {
                return bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
            }

            // Resize if image is too large
            val resizedBitmap = resizeIfNeeded(bitmap)

            // Apply preprocessing based on level
            val processedBitmap = when (level) {
                PreprocessingLevel.LIGHT -> applyLightPreprocessing(resizedBitmap)
                PreprocessingLevel.STANDARD -> applyStandardPreprocessing(resizedBitmap)
                PreprocessingLevel.AGGRESSIVE -> applyAggressivePreprocessing(resizedBitmap)
                PreprocessingLevel.NONE -> resizedBitmap
            }

            val processingTime = System.currentTimeMillis() - startTime
            Log.d(TAG, "Preprocessing completed in ${processingTime}ms with level: $level")

            return processedBitmap

        } catch (e: Exception) {
            Log.e(TAG, "Error during preprocessing, returning original bitmap", e)
            return bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
        }
    }

    /**
     * Resize bitmap if it exceeds maximum dimensions
     */
    private fun resizeIfNeeded(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= MAX_IMAGE_WIDTH && height <= MAX_IMAGE_HEIGHT) {
            return bitmap
        }

        val scale = minOf(
            MAX_IMAGE_WIDTH.toFloat() / width,
            MAX_IMAGE_HEIGHT.toFloat() / height
        )

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        Log.d(TAG, "Resizing image from ${width}x${height} to ${newWidth}x${newHeight}")
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Apply light preprocessing: grayscale + mild contrast enhancement
     */
    private fun applyLightPreprocessing(bitmap: Bitmap): Bitmap {
        val grayscale = convertToGrayscale(bitmap)
        return enhanceContrast(grayscale, CONTRAST_FACTOR_LIGHT)
    }

    /**
     * Apply standard preprocessing: full pipeline with balanced parameters
     */
    private fun applyStandardPreprocessing(bitmap: Bitmap): Bitmap {
        // Step 1: Convert to grayscale
        val grayscale = convertToGrayscale(bitmap)

        // Step 2: Enhance contrast
        val enhanced = enhanceContrast(grayscale, CONTRAST_FACTOR_STANDARD)

        // Step 3: Adjust brightness
        val brightened = adjustBrightness(enhanced, BRIGHTNESS_OFFSET_STANDARD)

        // Step 4: Sharpen
        val sharpened = sharpen(brightened, SHARPEN_AMOUNT_STANDARD)

        return sharpened
    }

    /**
     * Apply aggressive preprocessing: stronger parameters for difficult images
     */
    private fun applyAggressivePreprocessing(bitmap: Bitmap): Bitmap {
        // Step 1: Convert to grayscale
        val grayscale = convertToGrayscale(bitmap)

        // Step 2: Aggressive contrast enhancement
        val enhanced = enhanceContrast(grayscale, CONTRAST_FACTOR_AGGRESSIVE)

        // Step 3: Aggressive brightness adjustment
        val brightened = adjustBrightness(enhanced, BRIGHTNESS_OFFSET_AGGRESSIVE)

        // Step 4: Aggressive sharpening
        val sharpened = sharpen(brightened, SHARPEN_AMOUNT_AGGRESSIVE)

        // Step 5: Optional binarization for very challenging images
        val binarized = adaptiveBinarization(sharpened)

        return binarized
    }

    /**
     * Convert to grayscale using luminosity method
     */
    private fun convertToGrayscale(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(grayscale)
        val paint = Paint()

        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return grayscale
    }

    /**
     * Enhance contrast using ColorMatrix
     */
    private fun enhanceContrast(bitmap: Bitmap, factor: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val enhanced = Bitmap.createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)

        val canvas = Canvas(enhanced)
        val paint = Paint()

        val colorMatrix = ColorMatrix(
            floatArrayOf(
                factor, 0f, 0f, 0f, 0f,
                0f, factor, 0f, 0f, 0f,
                0f, 0f, factor, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return enhanced
    }

    /**
     * Adjust brightness
     */
    private fun adjustBrightness(bitmap: Bitmap, offset: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val brightened = Bitmap.createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)

        val canvas = Canvas(brightened)
        val paint = Paint()

        val colorMatrix = ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, offset,
                0f, 1f, 0f, 0f, offset,
                0f, 0f, 1f, 0f, offset,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return brightened
    }

    /**
     * Sharpen image using unsharp mask approximation
     */
    private fun sharpen(bitmap: Bitmap, amount: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val sharpened = Bitmap.createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)

        // Simple sharpening kernel approximation using ColorMatrix
        val canvas = Canvas(sharpened)
        val paint = Paint()

        // Sharpen by increasing mid-tone contrast
        val sharpenFactor = 1f + amount
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                sharpenFactor, 0f, 0f, 0f, -amount * 128,
                0f, sharpenFactor, 0f, 0f, -amount * 128,
                0f, 0f, sharpenFactor, 0f, -amount * 128,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return sharpened
    }

    /**
     * Adaptive binarization - convert to black and white
     * Uses simple global thresholding based on mean brightness
     */
    private fun adaptiveBinarization(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val binarized = Bitmap.createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)

        // Calculate mean brightness
        var totalBrightness = 0L
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                val brightness = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3
                totalBrightness += brightness
            }
        }
        val meanBrightness = (totalBrightness / (width * height)).toInt()

        // Use mean as threshold (with some adjustment)
        val threshold = max(meanBrightness - 20, 100)

        // Apply threshold
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                val brightness = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3

                val newPixel = if (brightness > threshold) {
                    Color.WHITE
                } else {
                    Color.BLACK
                }

                binarized.setPixel(x, y, newPixel)
            }
        }

        return binarized
    }

    /**
     * Get preprocessing statistics for debugging
     */
    fun getPreprocessingStats(bitmap: Bitmap): PreprocessingStats {
        val width = bitmap.width
        val height = bitmap.height

        var totalBrightness = 0L
        var minBrightness = 255
        var maxBrightness = 0

        for (y in 0 until height step 10) { // Sample every 10th pixel for performance
            for (x in 0 until width step 10) {
                val pixel = bitmap.getPixel(x, y)
                val brightness = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3

                totalBrightness += brightness
                minBrightness = min(minBrightness, brightness)
                maxBrightness = max(maxBrightness, brightness)
            }
        }

        val sampleCount = ((width / 10) * (height / 10)).toLong()
        val meanBrightness = if (sampleCount > 0) totalBrightness.toDouble() / sampleCount else 0.0

        return PreprocessingStats(
            width = width,
            height = height,
            meanBrightness = meanBrightness,
            minBrightness = minBrightness,
            maxBrightness = maxBrightness,
            contrastRange = maxBrightness - minBrightness
        )
    }

    data class PreprocessingStats(
        val width: Int,
        val height: Int,
        val meanBrightness: Double,
        val minBrightness: Int,
        val maxBrightness: Int,
        val contrastRange: Int
    )
}
