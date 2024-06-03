import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.isalatapp.databinding.FragmentCameraBinding
import com.isalatapp.model.getImageUri
import com.isalatapp.model.getVideoUri

class CameraFragment : Fragment() {
    private var _binding:  FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private var currentImageUri: Uri? = null
    private var currentVideoUri: Uri? = null

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
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
            requestPermissionLauncher.launch(REQUIRED_PERMISSION_STORAGE)
        }

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnVideo.setOnClickListener { startVideoRecording() }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startVideoRecording() {
        currentVideoUri = getVideoUri(requireContext())
        launcherIntentVideo.launch(currentVideoUri)
    }

    private val launcherIntentVideo = registerForActivityResult(
        ActivityResultContracts.CaptureVideo()
    ) { isSuccess ->
        if (isSuccess) {
            showVideo()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
            updatePreviewVisibility(true)
        }
    }

    private fun showVideo() {
        currentVideoUri?.let {
            Log.d("Video URI", "showVideo: $it")
            binding.previewVideoView.setVideoURI(it)
            binding.previewVideoView.start()
            updatePreviewVisibility(false)
        }
    }

    private fun updatePreviewVisibility(isImage: Boolean) {
        binding.previewImageView.visibility = if (isImage) View.VISIBLE else View.GONE
        binding.previewVideoView.visibility = if (isImage) View.GONE else View.VISIBLE
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
}
