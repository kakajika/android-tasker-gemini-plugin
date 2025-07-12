package dev.meinside.taskergeminiplugin.image

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginsample.tasker.ActivityConfigTasker
import dev.meinside.taskergeminiplugin.databinding.ActivityImageConfigBinding

class ActivityImageConfig : ActivityConfigTasker<ImageInput, ImageOutput, ImageRunner, ImageActionHelper, ActivityImageConfigBinding>() {
    
    private var selectedImageUri: Uri? = null
    private var savedImagePath: String? = null
    private val IMAGE_PICK_REQUEST = 1001
    
    override fun getNewHelper(config: TaskerPluginConfig<ImageInput>) = ImageActionHelper(this)
    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityImageConfigBinding = 
        ActivityImageConfigBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = inflateBinding(layoutInflater)
        if (!isConfigurable) {
            taskerHelper.finishForTasker()
            return
        }
        binding?.root?.let { setContentView(it) }
        
        setupUI()
        handleIncomingImage()
        
        taskerHelper.onCreate()
    }
    
    private fun setupUI() {
        binding?.buttonSelectImage?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_REQUEST)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                displaySelectedImage(uri)
            }
        }
    }
    
    private fun handleIncomingImage() {
        val intentData = intent
        if (intentData?.action == Intent.ACTION_SEND && intentData.type?.startsWith("image/") == true) {
            (intentData.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))?.let { uri ->
                selectedImageUri = uri
                displaySelectedImage(uri)
                Toast.makeText(this, "Image received for Gemini processing", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun displaySelectedImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            binding?.imageViewPreview?.setImageBitmap(bitmap)
            
            // Copy image to internal storage for persistent access
            savedImagePath = copyImageToInternalStorage(uri)
            
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun copyImageToInternalStorage(sourceUri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(sourceUri)
            val fileName = "gemini_image_${System.currentTimeMillis()}.jpg"
            val imageFile = File(filesDir, fileName)
            
            val outputStream = FileOutputStream(imageFile)
            inputStream?.copyTo(outputStream)
            
            inputStream?.close()
            outputStream.close()
            
            imageFile.absolutePath
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    override val inputForTasker: TaskerInput<ImageInput>
        get() = TaskerInput(ImageInput(
            binding?.editTextModel?.text?.toString() ?: "",
            binding?.editTextApiKey?.text?.toString() ?: "",
            binding?.editTextPrompt?.text?.toString() ?: "",
            savedImagePath ?: ""))

    override fun assignFromInput(input: TaskerInput<ImageInput>): Unit = input.regular.run {
        binding?.editTextModel?.setText(model)
        binding?.editTextApiKey?.setText(apiKey)
        binding?.editTextPrompt?.setText(prompt)
        
        if (imageUri.isNotEmpty()) {
            savedImagePath = imageUri
            displaySavedImage(imageUri)
        }
    }
    
    private fun displaySavedImage(imagePath: String) {
        try {
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imagePath)
                binding?.imageViewPreview?.setImageBitmap(bitmap)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load saved image", Toast.LENGTH_SHORT).show()
        }
    }
}