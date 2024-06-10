package com.isalatapp.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.isalatapp.R
import com.isalatapp.databinding.FragmentCameraXBinding
import com.isalatapp.model.getImageUri
import com.isalatapp.ui.customview.OverlayView
import java.io.File
import java.io.FileOutputStream

class CameraXFragment : Fragment() {

    private var _binding: FragmentCameraXBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val viewModel: CameraXViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(context, "Permission request granted", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        viewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            uri?.let { showVideo(it) }
        }

        viewModel.isRecording.observe(viewLifecycleOwner) { isRecording ->
            if (isRecording) {
                binding.recordingIndicator.visibility = View.VISIBLE
                val animatedDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.anim_recording) as? AnimatedVectorDrawable
                binding.recordingIndicator.setImageDrawable(animatedDrawable)
                animatedDrawable?.start()
                binding.recordingDuration.visibility = View.VISIBLE
            } else {
                binding.recordingIndicator.visibility = View.GONE
                binding.recordingDuration.visibility = View.GONE
                val animatedDrawable = binding.recordingIndicator.drawable as? AnimatedVectorDrawable
                animatedDrawable?.stop()
            }
        }

        viewModel.recordingDuration.observe(viewLifecycleOwner) { duration ->
            binding.recordingDuration.text = duration
        }
        startCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

        val videoCapture = VideoCapture.withOutput(recorder)
        viewModel.setVideoCapture(videoCapture)

        val previewView = binding.previewView
        preview.setSurfaceProvider(previewView.surfaceProvider)

        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, videoCapture)
    }

    private fun captureVideo() {
        val file = File(requireContext().externalMediaDirs.first(), "${System.currentTimeMillis()}.mp4")
        val outputOptions = FileOutputOptions.Builder(file).build()
        viewModel.startRecording(requireContext(), file, outputOptions) { savedFile ->
            showVideo(Uri.fromFile(savedFile))
        }
    }

    private fun showVideo(uri: Uri) {
        val file = uriToFile(uri, requireContext())
        binding.previewVideoView.apply {
            setVideoURI(uri)
            setOnCompletionListener {
                start() // Replay the video when it finishes
            }
            visibility = View.VISIBLE
            start()
        }
        binding.uploadButton.visibility = View.VISIBLE
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val contentResolver = context.contentResolver
        val file = File(context.cacheDir, "temp_video.mp4")
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input!!.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
        return file
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.setImageUri(uri)
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        requireContext(), REQUIRED_PERMISSION_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
}


