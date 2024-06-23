package com.isalatapp.yolov8tflite.isalat

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.isalatapp.yolov8tflite.BoundingBox
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class IsalatObjectDetector(
    private val context: Context,
    private val modelPath: String,
    private val labelPath: String,
    private val detectorListener: DetectorListener,
) {

    private var interpreter: Interpreter? = null
    private var labels = mutableListOf<String>()

    private var tensorWidth = 0
    private var tensorHeight = 0
    private var numChannel = 0
    private var numElements = 0
    private var isClassificationModel = false

    private val imageProcessor = ImageProcessor.Builder()
        .add(NormalizeOp(INPUT_MEAN, INPUT_STANDARD_DEVIATION))
        .add(CastOp(INPUT_IMAGE_TYPE))
        .build()

    fun setup(isGpu: Boolean = true) {
        if (interpreter != null) {
            close()
        }

        val options = if (isGpu) {
            val compatList = CompatibilityList()

            Interpreter.Options().apply {
                if (compatList.isDelegateSupportedOnThisDevice) {
                    val delegateOptions = compatList.bestOptionsForThisDevice
                    this.addDelegate(GpuDelegate(delegateOptions))
                } else {
                    this.setNumThreads(4)
                }
            }
        } else {
            Interpreter.Options().apply {
                this.setNumThreads(4)
            }
        }

        val model = FileUtil.loadMappedFile(context, modelPath)
        interpreter = Interpreter(model, options)

        val inputShape = interpreter?.getInputTensor(0)?.shape() ?: return
        val outputShape = interpreter?.getOutputTensor(0)?.shape() ?: return

        Log.d("IsalatObjectDetector", "Input shape: ${inputShape.contentToString()}")
        Log.d("IsalatObjectDetector", "Output shape: ${outputShape.contentToString()}")

        tensorWidth = inputShape[1]
        tensorHeight = inputShape[2]

        if (outputShape.size == 2 && outputShape[1] == 26) {
            // Treat as classification model
            isClassificationModel = true
            numChannel = 26
            numElements = 1
        } else if (outputShape.size == 3) {
            // Treat as detection model
            isClassificationModel = false
            numChannel = outputShape[2]
            numElements = outputShape[1]
        } else {
            throw IllegalArgumentException("Unexpected output tensor shape: ${outputShape.contentToString()}")
        }

        try {
            val inputStream: InputStream = context.assets.open(labelPath)
            val reader = BufferedReader(InputStreamReader(inputStream))

            var line: String? = reader.readLine()
            while (line != null && line != "") {
                labels.add(line)
                line = reader.readLine()
            }

            reader.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }

    fun detect(frame: Bitmap) {
        interpreter ?: return
        if (tensorWidth == 0) return
        if (tensorHeight == 0) return
        if (numChannel == 0) return
        if (numElements == 0) return

        var inferenceTime = SystemClock.uptimeMillis()

        val resizedBitmap = Bitmap.createScaledBitmap(frame, tensorWidth, tensorHeight, false)

        val tensorImage = TensorImage(INPUT_IMAGE_TYPE)
        tensorImage.load(resizedBitmap)
        val processedImage = imageProcessor.process(tensorImage)
        val imageBuffer = processedImage.buffer

        if (isClassificationModel) {
            // Classification
            val output = TensorBuffer.createFixedSize(intArrayOf(1, labels.size), OUTPUT_IMAGE_TYPE)
            interpreter?.run(imageBuffer, output.buffer)

            val outputArray = output.floatArray
            val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1
            val predictedLabel = labels.getOrNull(maxIndex) ?: "Unknown"

            // Create dummy bounding box for visualization
            val boundingBox = BoundingBox(
                0.1f,
                0.1f,
                0.9f,
                0.9f,
                0.5f,
                0.5f,
                0.8f,
                0.8f,
                1.0f,
                maxIndex,
                predictedLabel
            )

            inferenceTime = SystemClock.uptimeMillis() - inferenceTime
            detectorListener.onDetect(listOf(boundingBox), inferenceTime)

        } else {
            // Object detection
            val output = TensorBuffer.createFixedSize(
                intArrayOf(1, numElements, numChannel),
                OUTPUT_IMAGE_TYPE
            )
            interpreter?.run(imageBuffer, output.buffer)

            val boundingBoxes = bestBox(output.floatArray)
            inferenceTime = SystemClock.uptimeMillis() - inferenceTime

            if (boundingBoxes == null) {
                detectorListener.onEmptyDetect()
                return
            }

            // Filter bounding boxes based on confidence and size
            val filteredBoxes = boundingBoxes.filter {
                it.cnf > CONFIDENCE_THRESHOLD && it.w > MIN_BOX_SIZE && it.h > MIN_BOX_SIZE
            }

            if (filteredBoxes.isEmpty()) {
                detectorListener.onEmptyDetect()
                return
            }

            detectorListener.onDetect(filteredBoxes, inferenceTime)
        }
    }

    private fun bestBox(array: FloatArray): List<BoundingBox>? {
        val boundingBoxes = mutableListOf<BoundingBox>()

        for (i in 0 until numElements) {
            val score = array[i * numChannel + 4]
            if (score > CONFIDENCE_THRESHOLD) {
                val clsIdx = array.copyOfRange(
                    i * numChannel + 5,
                    i * numChannel + numChannel
                ).indices.maxByOrNull { array[i * numChannel + 5 + it] } ?: -1
                val clsName = labels.getOrNull(clsIdx) ?: continue

                val cx = array[i * numChannel]
                val cy = array[i * numChannel + 1]
                val w = array[i * numChannel + 2]
                val h = array[i * numChannel + 3]
                val x1 = cx - (w / 2f)
                val y1 = cy - (h / 2f)
                val x2 = cx + (w / 2f)
                val y2 = cy + (h / 2f)

                // Filter out bounding boxes that are too small
                if (w < MIN_BOX_SIZE || h < MIN_BOX_SIZE) continue

                boundingBoxes.add(
                    BoundingBox(
                        x1 = x1, y1 = y1, x2 = x2, y2 = y2,
                        cx = cx, cy = cy, w = w, h = h,
                        cnf = score, cls = clsIdx, clsName = clsName
                    )
                )
            }
        }

        if (boundingBoxes.isEmpty()) return null

        return applyNMS(boundingBoxes)
    }

    private fun applyNMS(boxes: List<BoundingBox>): List<BoundingBox> {
        val sortedBoxes = boxes.sortedByDescending { it.cnf }.toMutableList()
        val selectedBoxes = mutableListOf<BoundingBox>()

        while (sortedBoxes.isNotEmpty()) {
            val first = sortedBoxes.first()
            selectedBoxes.add(first)
            sortedBoxes.remove(first)

            val iterator = sortedBoxes.iterator()
            while (iterator.hasNext()) {
                val nextBox = iterator.next()
                val iou = calculateIoU(first, nextBox)
                if (iou >= IOU_THRESHOLD) {
                    iterator.remove()
                }
            }
        }

        return selectedBoxes
    }

    private fun calculateIoU(box1: BoundingBox, box2: BoundingBox): Float {
        val x1 = maxOf(box1.x1, box2.x1)
        val y1 = maxOf(box1.y1, box2.y1)
        val x2 = minOf(box1.x2, box2.x2)
        val y2 = minOf(box1.y2, box2.y2)
        val intersectionArea = maxOf(0F, x2 - x1) * maxOf(0F, y2 - y1)
        val box1Area = box1.w * box1.h
        val box2Area = box2.w * box2.h
        return intersectionArea / (box1Area + box2Area - intersectionArea)
    }

    interface DetectorListener {
        fun onEmptyDetect()
        fun onDetect(boundingBoxes: List<BoundingBox>, inferenceTime: Long)
    }

    companion object {
        private const val INPUT_MEAN = 0f
        private const val INPUT_STANDARD_DEVIATION = 1f
        private val INPUT_IMAGE_TYPE = DataType.FLOAT32
        private val OUTPUT_IMAGE_TYPE = DataType.FLOAT32
        private const val CONFIDENCE_THRESHOLD = 0.3F
        private const val IOU_THRESHOLD = 0.5F
        private const val MIN_BOX_SIZE =
            0.1F // Minimum size of bounding box to consider (relative to image size)
    }
}
