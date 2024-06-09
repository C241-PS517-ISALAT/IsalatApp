package com.isalatapp.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.isalatapp.R
import com.isalatapp.databinding.FragmentCameraXBinding
import com.isalatapp.model.getImageUri
import com.isalatapp.ui.customview.OverlayView
import java.io.File

class CameraXFragment : Fragment() {

    private var _binding: FragmentCameraXBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val REQUEST_CAMERA_PERMISSION = 1001

    private var videoCapture: VideoCapture<Recorder>? = null
    private var activeRecording: Recording? = null
    private var recordingStartTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(), REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(), REQUIRED_PERMISSION_STORAGE
                ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraXBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            requestPermissionLauncher.launch(REQUIRED_PERMISSION_STORAGE)
        }

        binding.buttonGallery.setOnClickListener { startGallery() }
        binding.buttonCapture.setOnClickListener { captureVideo() }
        requestCameraPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(updateDurationRunnable)
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()

        videoCapture = VideoCapture.withOutput(recorder)

        val previewView = binding.previewView
        preview.setSurfaceProvider(previewView.surfaceProvider)

        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, videoCapture)
    }

    private fun captureVideo() {
        val videoCapture = videoCapture ?: return

        val file = File(requireContext().externalMediaDirs.first(), "${System.currentTimeMillis()}.mp4")
        val outputOptions = FileOutputOptions.Builder(file).build()

        if (activeRecording != null) {
            activeRecording?.stop()
            activeRecording = null
            binding.recordingDuration.visibility = View.GONE
            binding.recordingIndicator.visibility = View.GONE
            (binding.recordingIndicator.drawable as? Animatable)?.stop()
            handler.removeCallbacks(updateDurationRunnable)
            showVideo(file)
        } else {
            activeRecording = videoCapture.output
                .prepareRecording(requireContext(), outputOptions)
                .start(ContextCompat.getMainExecutor(requireContext())) { recordEvent ->
                    when (recordEvent) {
                        is VideoRecordEvent.Start -> {
                            recordingStartTime = System.currentTimeMillis()
                            binding.recordingDuration.visibility = View.VISIBLE
                            binding.recordingIndicator.visibility = View.VISIBLE
                            binding.recordingIndicator.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.anim_recording))
                            (binding.recordingIndicator.drawable as? Animatable)?.start()
                            handler.post(updateDurationRunnable)
                            Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show()
                        }
                        is VideoRecordEvent.Finalize -> {
                            if (!recordEvent.hasError()) {
                                Toast.makeText(context, "Recording saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
                            } else {
                                file.delete()
                                Toast.makeText(context, "Recording error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
        }
    }

    private val updateDurationRunnable = object : Runnable {
        override fun run() {
            val elapsedSeconds = (System.currentTimeMillis() - recordingStartTime) / 1000
            val minutes = elapsedSeconds / 60
            val seconds = elapsedSeconds % 60
            binding.recordingDuration.text = String.format("%02d:%02d", minutes, seconds)
            handler.postDelayed(this, 1000)
        }
    }

    private fun showVideo(file: File) {
        binding.previewVideoView.apply {
            setVideoURI(Uri.fromFile(file))
            visibility = View.VISIBLE
            start()
        }
        binding.uploadButton.visibility = View.VISIBLE
    }
    private fun showVideo() {
        currentImageUri?.let {
            Log.d("Video URI", "showImage: $it")
            binding.previewVideoView.setVideoURI(it)
            binding.previewVideoView.visibility = View.VISIBLE
            binding.uploadButton.visibility = View.VISIBLE
        }
    }
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showVideo()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showVideo()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to use this feature", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
}



