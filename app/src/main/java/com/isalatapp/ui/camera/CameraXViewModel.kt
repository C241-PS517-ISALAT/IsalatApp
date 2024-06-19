package com.isalatapp.ui.camera

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isalatapp.yolov8tflite.BoundingBox
import java.io.File

class CameraXViewModel : ViewModel() {

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    private val _isRecording = MutableLiveData<Boolean>(false)
    val isRecording: LiveData<Boolean> = _isRecording

    private val _recordingDuration = MutableLiveData<String>("00:00")
    val recordingDuration: LiveData<String> = _recordingDuration

    private val handler = Handler(Looper.getMainLooper())
    private var recordingStartTime: Long = 0
    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null

    private val updateDurationRunnable = object : Runnable {
        override fun run() {
            val elapsedSeconds = (System.currentTimeMillis() - recordingStartTime) / 1000
            val minutes = elapsedSeconds / 60
            val seconds = elapsedSeconds % 60
            _recordingDuration.postValue(String.format("%02d:%02d", minutes, seconds))
            handler.postDelayed(this, 1000)
        }
    }

    fun setVideoCapture(videoCapture: VideoCapture<Recorder>?) {
        this.videoCapture = videoCapture
    }

    fun startRecording(
        context: Context,
        file: File,
        outputOptions: FileOutputOptions,
        onFinish: (file: File) -> Unit
    ) {
        val videoCapture = videoCapture ?: return
        if (activeRecording != null) {
            stopRecording()
            return
        }

        activeRecording = videoCapture.output
            .prepareRecording(context, outputOptions)
            .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        recordingStartTime = System.currentTimeMillis()
                        _isRecording.postValue(true)
                        handler.post(updateDurationRunnable)
                    }

                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            onFinish(file)
                        } else {
                            file.delete()
                        }
                        _isRecording.postValue(false)
                    }
                }
            }
    }

    fun stopRecording() {
        activeRecording?.stop()
        activeRecording = null
        handler.removeCallbacks(updateDurationRunnable)
        _isRecording.postValue(false)
    }

    fun setImageUri(uri: Uri?) {
        _currentImageUri.postValue(uri)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateDurationRunnable)
    }


    private val _boundingBox = MutableLiveData<BoundingBox>()
    val boundingBox: LiveData<BoundingBox> get() = _boundingBox
    fun updateBoundingBox(newBoundingBox: BoundingBox) {
        _boundingBox.value = newBoundingBox
    }
}
