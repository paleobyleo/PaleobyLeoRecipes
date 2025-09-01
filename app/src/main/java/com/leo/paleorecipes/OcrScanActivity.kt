package com.leo.paleorecipes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.leo.paleorecipes.ui.theme.PaleoRecipesTheme
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.InputStream

@AndroidEntryPoint
class OcrScanActivity : ComponentActivity() {
    private val TAG = "OcrScanActivity"

    // Request code for camera permission
    private val REQUEST_CAMERA_PERMISSION = 1001

    // Activity result launcher for camera
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            processImage(it)
        }
    }

    // Activity result launcher for gallery
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            processImageFromUri(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PaleoRecipesTheme {
                OcrScanScreen(
                    onBackClick = { finish() },
                    onScanFromCamera = { requestCameraPermissionAndLaunch() },
                    onScanFromGallery = { launchGallery() },
                )
            }
        }
    }

    private fun requestCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                // Request camera permission
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION,
                )
            }
        }
    }

    private fun launchCamera() {
        try {
            cameraLauncher.launch(null)
        } catch (e: Exception) {
            Log.e(TAG, "Error launching camera: ${e.message}", e)
            Toast.makeText(this, "Error launching camera: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun launchGallery() {
        try {
            galleryLauncher.launch("image/*")
        } catch (e: Exception) {
            Log.e(TAG, "Error launching gallery: ${e.message}", e)
            Toast.makeText(this, "Error launching gallery: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun processImage(bitmap: Bitmap) {
        // Process the image with ML Kit OCR
        recognizeTextFromImage(bitmap)
    }

    private fun processImageFromUri(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Rotate bitmap if needed
            val rotatedBitmap = rotateBitmap(bitmap, uri)
            processImage(rotatedBitmap)
        } catch (e: Exception) {
            Log.e(TAG, "Error processing image from URI: ${e.message}", e)
            Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, uri: Uri): Bitmap {
        try {
            val inputStream: InputStream = contentResolver.openInputStream(uri)!!
            val exif = ExifInterface(inputStream)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            inputStream.close()

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            Log.e(TAG, "Error rotating bitmap: ${e.message}", e)
            return bitmap
        }
    }

    private fun recognizeTextFromImage(bitmap: Bitmap) {
        Toast.makeText(this, "Processing image with OCR...", Toast.LENGTH_SHORT).show()

        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Extract recipe information from the recognized text
                    val recipeJson = extractRecipeFromText(visionText)

                    // Navigate to AddEditRecipeActivity with the OCR results
                    val intent = Intent(this, AddEditRecipeActivity::class.java).apply {
                        putExtra("ocr_recipe", recipeJson)
                    }
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Text recognition failed: ${e.message}", e)
                    Toast.makeText(this, "Failed to process image: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } catch (e: UnsatisfiedLinkError) {
            // Handle case where native libraries are not available (e.g., on 16 KB devices)
            Log.e(TAG, "ML Kit native library not available: ${e.message}", e)
            Toast.makeText(this, "OCR functionality not available on this device. Please try manual entry.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e(TAG, "Text recognition failed: ${e.message}", e)
            Toast.makeText(this, "Failed to process image: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun extractRecipeFromText(visionText: Text): String {
        val textBlocks = visionText.textBlocks
        Log.d(TAG, "Found ${textBlocks.size} text blocks")

        if (textBlocks.isEmpty()) {
            Log.d(TAG, "No text blocks found, returning empty recipe JSON")
            return createEmptyRecipeJson()
        }

        // Log detailed information about what we found
        textBlocks.forEachIndexed { index, block ->
            Log.d(TAG, "Block $index: '${block.text}'")
            block.lines.forEachIndexed { lineIndex, line ->
                Log.d(TAG, "  Line $lineIndex: '${line.text}'")
                line.elements.forEachIndexed { elementIndex, element ->
                    Log.d(TAG, "    Element $elementIndex: '${element.text}'")
                }
            }
        }

        // Try multiple approaches to extract text and use the one with the most content
        val blockText = buildString {
            textBlocks.forEach { block ->
                if (block.text.isNotBlank()) {
                    append(block.text)
                    append("\n\n")
                }
            }
        }

        val lineText = buildString {
            textBlocks.forEach { block ->
                block.lines.forEach { line ->
                    if (line.text.isNotBlank()) {
                        append(line.text)
                        append("\n")
                    }
                }
                append("\n") // Extra newline between blocks
            }
        }

        val elementText = buildString {
            textBlocks.forEach { block ->
                block.lines.forEach { line ->
                    line.elements.forEach { element ->
                        if (element.text.isNotBlank()) {
                            append(element.text)
                            append(" ")
                        }
                    }
                }
                append("\n\n") // Extra newlines between blocks
            }
        }

        // Choose the approach that gives us the most text
        val texts = listOf(
            "Block text" to blockText,
            "Line text" to lineText,
            "Element text" to elementText,
        )

        val (method, fullText) = texts.maxByOrNull { it.second.length } ?: ("None" to "")

        Log.d(TAG, "Text extraction method: $method")
        Log.d(TAG, "Extracted text length: ${fullText.length}")
        Log.d(TAG, "Extracted text preview: ${fullText.take(200)}")

        // Create a more user-friendly message when OCR results are limited
        val finalText = if (fullText.isBlank() || fullText.length < 20) {
            """
            OCR SCAN RESULTS - LIMITED TEXT DETECTED
            ======================================

            The OCR engine detected very limited text in your image:
            "$fullText"

            This could be due to:
            - Poor image quality or lighting
            - Text that is too small or blurry
            - Fancy or decorative fonts that are hard to recognize
            - Handwriting which is more difficult to process
            - Text in a language or script that is not well-supported

            Tips for better OCR results:
            1. Ensure good lighting when taking the photo
            2. Keep the camera steady and parallel to the text
            3. Make sure the text is in focus and not blurry
            4. Try to capture the entire recipe text in the frame
            5. Use a clear, standard font if possible
            6. Avoid shadows or glare on the text

            You can still save this recipe and manually edit the title and description.
            """.trimIndent()
        } else {
            fullText
        }

        // Create a simple JSON with the full text in description and a placeholder title
        val recipeJson = JSONObject().apply {
            put("title", "Scanned Recipe - Please Edit Title")
            put("description", finalText.trim())
            put("ingredients", mutableListOf<String>())
            put("instructions", mutableListOf<String>())
            put("prepTime", 0)
            put("cookTime", 0)
            put("servings", 1)
            put("category", "Scanned")
        }

        Log.d(TAG, "Created recipe JSON with full text in description")
        return recipeJson.toString()
    }

    private fun createEmptyRecipeJson(): String {
        return """
            {
                "title": "Scanned Recipe - Please Edit Title",
                "description": "Recipe scanned using OCR. Please edit this field with your recipe details.",
                "ingredients": [],
                "instructions": [],
                "prepTime": 0,
                "cookTime": 0,
                "servings": 1,
                "category": "Scanned"
            }
        """.trimIndent()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera()
                } else {
                    Toast.makeText(this, "Camera permission is required to scan recipes", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScanScreen(
    onBackClick: () -> Unit,
    onScanFromCamera: () -> Unit,
    onScanFromGallery: () -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Scan Recipe",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E1E),
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header text
            Text(
                text = "Scan Recipe from Paper",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 32.dp),
            )

            // Description
            Text(
                text = "Capture a photo of your recipe or select one from your gallery to extract the ingredients and instructions using OCR technology.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier
                    .padding(bottom = 32.dp),
            )

            // Illustration or icon
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        color = Color(0xFF2C2C2C),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .border(
                        border = BorderStroke(2.dp, Color(0xFF8B4513)),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.DocumentScanner,
                    contentDescription = "Scan document",
                    tint = Color(0xFF8B4513),
                    modifier = Modifier.size(100.dp),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Camera scan button
            Button(
                onClick = onScanFromCamera,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B4513),
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                )
                Text(
                    text = "Scan with Camera",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            // Gallery button
            OutlinedButton(
                onClick = onScanFromGallery,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                ),
                border = BorderStroke(2.dp, Color(0xFF8B4513)),
                shape = RoundedCornerShape(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Collections,
                    contentDescription = "Gallery",
                    tint = Color(0xFF8B4513),
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                )
                Text(
                    text = "Select from Gallery",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Information text
            Text(
                text = "OCR technology will extract text from your recipe image and convert it into an editable format.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 16.dp),
            )
        }
    }
}
