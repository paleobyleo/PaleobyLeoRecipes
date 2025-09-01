package com.leo.paleorecipes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import android.util.Log
import com.leo.paleorecipes.data.Recipe
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RecipePrintAdapter(private val context: Context, private val recipe: Recipe) : PrintDocumentAdapter() {
    private val TAG = "RecipePrintAdapter"
    private var pdfDocument: PdfDocument? = null
    private var recipeBitmap: Bitmap? = null

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?,
    ) {
        Log.d(TAG, "onLayout started for recipe: ${recipe.title}")

        // Create a new PDF document
        pdfDocument = PrintedPdfDocument(context, newAttributes)

        // If cancellation was requested, inform the callback and return
        if (cancellationSignal?.isCanceled == true) {
            Log.d(TAG, "onLayout cancelled")
            callback.onLayoutCancelled()
            return
        }

        // Debug: List all files in the app's internal storage
        fun debugStorage(base: File, indent: String = "") {
            if (!base.exists()) {
                Log.d(TAG, "${indent}${base.name} (does not exist)")
                return
            }

            if (base.isDirectory) {
                Log.d(TAG, "${indent}${base.name}/ (directory)")
                base.listFiles()?.forEach { child ->
                    debugStorage(child, "$indent  ")
                } ?: Log.d(TAG, "$indent  (empty)")
            } else {
                Log.d(TAG, "${indent}${base.name} (${base.length()} bytes)")
            }
        }

        Log.d(TAG, "=== DEBUG: App Storage Structure ===")
        debugStorage(context.filesDir)
        Log.d(TAG, "=== END DEBUG ===\n")

        // Fix package name in image URL if needed
        var imageUrl = recipe.imageUrl?.replace("com.leeo.paleorecipes", "com.leo.paleorecipes")

        // Log the original and fixed URLs for debugging
        if (recipe.imageUrl != imageUrl) {
            Log.d(TAG, "Fixed package name in image URL")
            Log.d(TAG, "Original URL: ${recipe.imageUrl}")
            Log.d(TAG, "Fixed URL: $imageUrl")
        }

        // Log all possible storage locations
        Log.d(TAG, "=== Storage Locations ===")
        Log.d(TAG, "Files dir: ${context.filesDir.absolutePath}")
        Log.d(TAG, "Cache dir: ${context.cacheDir.absolutePath}")
        Log.d(TAG, "External files dir: ${context.getExternalFilesDir(null)?.absolutePath ?: "Not available"}")
        Log.d(TAG, "External cache dir: ${context.externalCacheDir?.absolutePath ?: "Not available"}")
        Log.d(TAG, "External media dir: ${context.getExternalMediaDirs().firstOrNull()?.absolutePath ?: "Not available"}")

        // Load the image synchronously if available
        if (!imageUrl.isNullOrEmpty()) {
            try {
                Log.d(TAG, "=== Starting image loading process ===")
                Log.d(TAG, "Original image URL: ${recipe.imageUrl}")

                // Check if it's a content URI
                if (recipe.imageUrl.startsWith("content://")) {
                    Log.d(TAG, "Detected content URI")
                    try {
                        val uri = android.net.Uri.parse(recipe.imageUrl)
                        Log.d(TAG, "Trying to load from content URI: $uri")

                        // First try with content resolver
                        context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                            Log.d(TAG, "Successfully opened file descriptor")

                            // Get image dimensions first
                            val options = android.graphics.BitmapFactory.Options()
                            options.inJustDecodeBounds = true
                            android.graphics.BitmapFactory.decodeFileDescriptor(
                                pfd.fileDescriptor,
                                null,
                                options,
                            )

                            Log.d(TAG, "Image dimensions: ${options.outWidth}x${options.outHeight}")

                            // Calculate sample size
                            options.inSampleSize = calculateInSampleSize(options, 1024, 1024)
                            options.inJustDecodeBounds = false
                            options.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888

                            // Load the actual bitmap
                            val fileDescriptor = android.os.ParcelFileDescriptor.dup(pfd.fileDescriptor)
                            try {
                                recipeBitmap = android.graphics.BitmapFactory.decodeFileDescriptor(
                                    fileDescriptor.fileDescriptor,
                                    null,
                                    options,
                                )

                                if (recipeBitmap != null) {
                                    Log.d(TAG, "✅ Successfully loaded image: ${recipeBitmap!!.width}x${recipeBitmap!!.height}")
                                    Log.d(TAG, "Bitmap config: ${recipeBitmap!!.config}")
                                } else {
                                    Log.e(TAG, "❌ Failed to decode bitmap from file descriptor")
                                }
                            } finally {
                                fileDescriptor.close()
                            }
                        } ?: Log.e(TAG, "❌ Failed to open file descriptor for content URI")
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Error loading from content URI: ${e.message}", e)
                    }
                }
                // Handle file paths
                else {
                    // List of possible paths to try
                    val possiblePaths = mutableListOf<String>()

                    // 1. Try the exact path as stored
                    possiblePaths.add(imageUrl)

                    // 2. Try with file:// prefix removed
                    if (imageUrl.startsWith("file:")) {
                        possiblePaths.add(android.net.Uri.parse(imageUrl).path ?: "")
                    }

                    // 3. Try with app's files directory
                    val fileName = imageUrl.substringAfterLast("/")
                    val recipeImagesDir = File(context.filesDir, "recipe_images")

                    // 4. Try with external storage
                    val externalFilesDir = context.getExternalFilesDir("recipe_images")
                    if (externalFilesDir != null) {
                        possiblePaths.add(File(externalFilesDir, fileName).absolutePath)
                        Log.d(TAG, "Added external files path: ${File(externalFilesDir, fileName).absolutePath}")
                    }

                    // 5. Try with external media directory
                    val externalMediaDir = context.getExternalMediaDirs().firstOrNull()
                    if (externalMediaDir != null) {
                        val mediaFile = File(externalMediaDir, "recipe_images/$fileName")
                        possiblePaths.add(mediaFile.absolutePath)
                        Log.d(TAG, "Added external media path: ${mediaFile.absolutePath}")
                    }

                    // Debug the recipe_images directory
                    Log.d(TAG, "=== Checking recipe_images directory ===")
                    Log.d(TAG, "Path: ${recipeImagesDir.absolutePath}")
                    Log.d(TAG, "Exists: ${recipeImagesDir.exists()}")
                    Log.d(TAG, "Is directory: ${recipeImagesDir.isDirectory}")
                    Log.d(TAG, "Is file: ${recipeImagesDir.isFile}")

                    // If it's a directory, list its contents
                    if (recipeImagesDir.isDirectory) {
                        Log.d(TAG, "=== Files in recipe_images directory ===")
                        recipeImagesDir.listFiles()?.forEach { file ->
                            Log.d(TAG, "- ${file.name} (${file.length()} bytes)")
                            // Add each file to possible paths
                            possiblePaths.add(file.absolutePath)
                        } ?: Log.e(TAG, "No files in recipe_images directory")
                    }
                    // If it's a file, log its contents (might be corrupted)
                    else if (recipeImagesDir.isFile) {
                        Log.e(TAG, "❌ recipe_images is a file, not a directory!")
                        Log.e(TAG, "File size: ${recipeImagesDir.length()} bytes")

                        // Try to read the file as text to see what's in it
                        try {
                            val content = recipeImagesDir.readText(Charsets.UTF_8)
                            Log.e(TAG, "File content (first 500 chars): ${content.take(500)}")
                        } catch (e: Exception) {
                            Log.e(TAG, "Could not read file content: ${e.message}")
                        }

                        // Try to recover by creating the directory
                        try {
                            recipeImagesDir.delete() // Remove the file
                            if (recipeImagesDir.mkdirs()) {
                                Log.d(TAG, "✅ Created recipe_images directory")
                            } else {
                                Log.e(TAG, "❌ Failed to create recipe_images directory")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error creating directory: ${e.message}")
                        }
                    }
                    // If it doesn't exist, create it
                    else {
                        Log.e(TAG, "recipe_images directory does not exist: ${recipeImagesDir.absolutePath}")
                        if (recipeImagesDir.mkdirs()) {
                            Log.d(TAG, "✅ Created recipe_images directory")
                        } else {
                            Log.e(TAG, "❌ Failed to create recipe_images directory")
                        }
                    }

                    // 4. Also try the direct path in case the file is stored directly in files dir
                    possiblePaths.add(File(context.filesDir, fileName).absolutePath)

                    // 5. Try with app's cache directory
                    possiblePaths.add(File(context.cacheDir, fileName).absolutePath)

                    // Try each path until we find a valid image
                    for (filePath in possiblePaths.distinct()) {
                        Log.d(TAG, "Trying path: $filePath")

                        try {
                            val file = File(filePath)
                            if (file.exists() && file.length() > 0) {
                                Log.d(TAG, "✅ File exists, size: ${file.length()} bytes")

                                // First, get the image dimensions
                                val options = android.graphics.BitmapFactory.Options()
                                options.inJustDecodeBounds = true
                                android.graphics.BitmapFactory.decodeFile(filePath, options)

                                Log.d(TAG, "Image dimensions: ${options.outWidth}x${options.outHeight}")

                                // Calculate sample size
                                options.inSampleSize = calculateInSampleSize(options, 1024, 1024)
                                options.inJustDecodeBounds = false
                                options.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888

                                // Load the bitmap
                                val bitmap = android.graphics.BitmapFactory.decodeFile(filePath, options)

                                if (bitmap != null) {
                                    recipeBitmap = bitmap
                                    Log.d(TAG, "✅ Successfully loaded image: ${bitmap.width}x${bitmap.height}")
                                    Log.d(TAG, "Bitmap config: ${bitmap.config}")
                                    break // Exit the loop if we successfully loaded the image
                                } else {
                                    Log.e(TAG, "❌ Failed to decode bitmap from file")
                                }
                            } else {
                                Log.e(TAG, "❌ File does not exist or is empty: $filePath")

                                // List files in the parent directory for debugging
                                val parentDir = file.parentFile
                                if (parentDir != null && parentDir.exists()) {
                                    val files = parentDir.listFiles()
                                    if (files != null && files.isNotEmpty()) {
                                        Log.d(TAG, "Files in directory ${parentDir.absolutePath}:")
                                        for (f in files) {
                                            Log.d(TAG, "- ${f.name} (${f.length()} bytes)")
                                        }
                                    } else {
                                        Log.e(TAG, "No files in directory")
                                    }
                                } else {
                                    Log.e(TAG, "Parent directory does not exist: ${parentDir?.absolutePath}")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "❌ Error processing file $filePath: ${e.message}")
                        }
                    }
                }

                if (recipeBitmap == null) {
                    Log.e(TAG, "❌ All image loading methods failed")
                    Log.e(TAG, "Image URL: ${recipe.imageUrl}")
                    Log.e(TAG, "Files dir: ${context.filesDir.absolutePath}")
                    Log.e(TAG, "Cache dir: ${context.cacheDir.absolutePath}")

                    // Fallback: Create a placeholder bitmap
                    try {
                        val width = 300 // Default width for placeholder
                        val height = 200 // Default height for placeholder
                        recipeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                        val canvas = Canvas(recipeBitmap!!)

                        // Draw a background
                        val paint = Paint().apply {
                            color = Color.LTGRAY
                            style = Paint.Style.FILL
                        }
                        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

                        // Draw text
                        paint.color = Color.DKGRAY
                        paint.textSize = 30f
                        paint.textAlign = Paint.Align.CENTER
                        val text = "No Image Available"
                        val x = width / 2f
                        val y = height / 2f - (paint.descent() + paint.ascent()) / 2
                        canvas.drawText(text, x, y, paint)

                        Log.d(TAG, "✅ Created placeholder image: ${width}x$height")
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Failed to create placeholder image: ${e.message}")
                    }
                } else {
                    Log.d(TAG, "✅✅✅ Successfully loaded bitmap: ${recipeBitmap!!.width}x${recipeBitmap!!.height}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing image: ${e.message}", e)
            }
        }

        // Create a document info object with basic metadata
        val info = PrintDocumentInfo.Builder("${recipe.title}.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(1) // For simplicity, we'll use just one page
            .build()

        Log.d(TAG, "onLayout completed, notifying callback")

        // Inform the callback that layout is complete
        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback,
    ) {
        Log.d(TAG, "onWrite started for recipe: ${recipe.title}")

        try {
            // Check for cancellation
            if (cancellationSignal?.isCanceled == true) {
                Log.d(TAG, "onWrite cancelled")
                callback.onWriteCancelled()
                return
            }

            // Create a page info object for the first page
            val pageInfo = PdfDocument.PageInfo.Builder(
                (8.5f * 72).toInt(), // 8.5 inches in points (72 points per inch)
                (11f * 72).toInt(), // 11 inches in points
                0, // Page index (0-based)
            ).create()

            // Start the page with the page info
            val page = pdfDocument?.startPage(pageInfo)

            // Get the canvas for drawing
            val canvas = page?.canvas
            if (canvas != null) {
                // Draw the recipe content
                drawRecipe(canvas)
            }

            // Finish the page
            pdfDocument?.finishPage(page)

            // Write the document to the destination
            pdfDocument?.writeTo(FileOutputStream(destination.fileDescriptor))

            Log.d(TAG, "PDF document written successfully")

            // Inform the callback that write is complete
            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: IOException) {
            Log.e(TAG, "Error writing PDF: ${e.message}", e)
            callback.onWriteFailed(e.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error in onWrite: ${e.message}", e)
            callback.onWriteFailed("Error: ${e.message}")
        } finally {
            // Clean up
            try {
                pdfDocument?.close()
                pdfDocument = null
            } catch (e: Exception) {
                Log.e(TAG, "Error closing PDF document: ${e.message}", e)
            }
        }
    }

    private fun drawRecipe(canvas: Canvas) {
        Log.d(TAG, "Drawing recipe content to PDF")

        try {
            // Calculate scale factor based on canvas width
            val scaleFactor = canvas.width / 1000f

            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 36f * scaleFactor
                isFakeBoldText = true
            }

            val subtitlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 24f * scaleFactor
                isFakeBoldText = true
            }

            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 18f * scaleFactor
            }

            val linePaint = Paint().apply {
                color = Color.LTGRAY
                style = Paint.Style.STROKE
                strokeWidth = 2f * scaleFactor
            }

            // Page dimensions
            val pageWidth = canvas.width
            val pageHeight = canvas.height

            // Calculate margins
            val horizontalMargin = pageWidth * 0.1f
            val verticalMargin = pageHeight * 0.05f

            // Start position for content
            var y = verticalMargin + (titlePaint.textSize / 2)

            // Draw Paleo logo at the top center (make it round)
            try {
                val logoBitmap = android.graphics.BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.paleo_logo,
                )

                if (logoBitmap != null) {
                    // Scale the logo to fit appropriately (about 10% of page width)
                    val logoWidth = pageWidth * 0.15f
                    val logoHeight = (logoBitmap.height.toFloat() / logoBitmap.width.toFloat()) * logoWidth

                    // Position the logo at the top center
                    val logoX = (pageWidth - logoWidth) / 2
                    val logoY = y

                    // Create a circular mask for the logo to make it round
                    val output = Bitmap.createBitmap(logoBitmap.width, logoBitmap.height, Bitmap.Config.ARGB_8888)
                    val canvasRound = Canvas(output)
                    val paintRound = Paint()
                    val rect = android.graphics.Rect(0, 0, logoBitmap.width, logoBitmap.height)
                    val rectF = android.graphics.RectF(rect)
                    val radius = logoBitmap.width.coerceAtMost(logoBitmap.height) / 2f

                    paintRound.isAntiAlias = true
                    canvasRound.drawARGB(0, 0, 0, 0)
                    paintRound.color = Color.BLACK
                    canvasRound.drawRoundRect(rectF, radius, radius, paintRound)

                    paintRound.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
                    canvasRound.drawBitmap(logoBitmap, rect, rect, paintRound)

                    // Draw the rounded logo
                    val srcRect = android.graphics.Rect(0, 0, output.width, output.height)
                    val destRect = android.graphics.RectF(logoX, logoY, logoX + logoWidth, logoY + logoHeight)
                    canvas.drawBitmap(output, srcRect, destRect, null)

                    // Update y position to below the logo with some spacing
                    y += logoHeight + (subtitlePaint.textSize * 1.5f)

                    // Recycle the bitmaps to free memory
                    logoBitmap.recycle()
                    output.recycle()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error drawing logo: ${e.message}", e)
                // Continue without logo if there's an error
            }

            // Draw title with more spacing
            val titleWidth = titlePaint.measureText(recipe.title)
            val titleX = (pageWidth - titleWidth) / 2
            y += titlePaint.textSize * 0.5f // Add space before title
            canvas.drawText(recipe.title, titleX, y, titlePaint)
            y += titlePaint.textSize * 1.8f // More space after title

            // Draw separator line with more spacing
            canvas.drawLine(horizontalMargin, y, pageWidth - horizontalMargin, y, linePaint)
            y += subtitlePaint.textSize * 1.5f

            // Draw image if available
            if (recipeBitmap != null) {
                try {
                    Log.d(TAG, "Drawing image to PDF")

                    try {
                        // Add extra space before the image
                        y += subtitlePaint.textSize * 1.2f

                        // Calculate maximum available dimensions
                        val maxImageWidth = pageWidth - (2 * horizontalMargin)
                        val maxImageHeight = pageHeight * 0.4f

                        // Calculate aspect ratio of the bitmap
                        val bitmapWidth = recipeBitmap!!.width.toFloat()
                        val bitmapHeight = recipeBitmap!!.height.toFloat()
                        val bitmapAspect = bitmapWidth / bitmapHeight

                        // Calculate target dimensions maintaining aspect ratio
                        var targetWidth = maxImageWidth
                        var targetHeight = targetWidth / bitmapAspect

                        // If the height is too large, scale down to fit
                        if (targetHeight > maxImageHeight) {
                            targetHeight = maxImageHeight
                            targetWidth = targetHeight * bitmapAspect
                        }

                        // Center the image horizontally
                        val imageX = (pageWidth - targetWidth) / 2

                        // Draw white background with rounded corners
                        val bgRect = android.graphics.RectF(
                            imageX - 8f * scaleFactor,
                            y - 8f * scaleFactor,
                            imageX + targetWidth + 8f * scaleFactor,
                            y + targetHeight + 8f * scaleFactor,
                        )

                        val bgPaint = Paint().apply {
                            color = Color.WHITE
                            style = Paint.Style.FILL
                            isAntiAlias = true
                        }

                        // Draw the background
                        canvas.drawRoundRect(bgRect, 12f * scaleFactor, 12f * scaleFactor, bgPaint)

                        // Draw the image
                        val destRect = android.graphics.RectF(
                            imageX,
                            y,
                            imageX + targetWidth,
                            y + targetHeight,
                        )

                        val srcRect = android.graphics.Rect(
                            0,
                            0,
                            recipeBitmap!!.width,
                            recipeBitmap!!.height,
                        )

                        // Draw the bitmap
                        canvas.drawBitmap(recipeBitmap!!, srcRect, destRect, null)

                        // Draw border
                        val borderPaint = Paint().apply {
                            color = Color.LTGRAY
                            style = Paint.Style.STROKE
                            strokeWidth = 1.5f * scaleFactor
                            isAntiAlias = true
                        }
                        canvas.drawRoundRect(bgRect, 12f * scaleFactor, 12f * scaleFactor, borderPaint)

                        // Update y position with space after image
                        y += targetHeight + (subtitlePaint.textSize * 1.5f)

                        // Add a subtle separator line after the image
                        canvas.drawLine(
                            horizontalMargin,
                            y,
                            pageWidth - horizontalMargin,
                            y,
                            linePaint,
                        )
                        y += subtitlePaint.textSize * 1.2f

                        Log.d(TAG, "Successfully drew image: ${targetWidth.toInt()}x${targetHeight.toInt()}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error drawing image: ${e.message}", e)

                        // Draw error message
                        val errorText = "[Image not available]"
                        val errorPaint = Paint().apply {
                            color = Color.RED
                            textSize = 12f * scaleFactor
                            isAntiAlias = true
                        }

                        val textWidth = errorPaint.measureText(errorText)
                        val textX = (pageWidth - textWidth) / 2

                        canvas.drawText(
                            errorText,
                            textX,
                            y + 20f * scaleFactor,
                            errorPaint,
                        )

                        // Still add some space to maintain layout
                        y += 40f * scaleFactor
                    }

                    Log.d(TAG, "Image drawn successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error drawing image: ${e.message}", e)
                    // Continue without the image
                    // Add some space even if image fails to load
                    y += subtitlePaint.textSize * 1.5f
                }
            }

            // Draw description if available
            if (recipe.description.isNotEmpty()) {
                // Add space before description
                y += textPaint.textSize * 0.8f

                val descriptionLines = splitTextIntoLines(
                    recipe.description,
                    textPaint,
                    pageWidth - 2 * horizontalMargin,
                )

                for (line in descriptionLines) {
                    canvas.drawText(line, horizontalMargin, y, textPaint)
                    y += textPaint.textSize * 1.5f // Increased line spacing
                }

                y += textPaint.textSize // More space after description
            }

            // Draw prep time, cook time, and servings if available
            val timeInfo = mutableListOf<String>()
            if (recipe.prepTime > 0) {
                timeInfo.add(context.getString(R.string.prep_time_format, recipe.prepTime))
            }
            if (recipe.cookTime > 0) {
                timeInfo.add(context.getString(R.string.cook_time_format, recipe.cookTime))
            }
            if (recipe.servings > 0) {
                timeInfo.add(context.getString(R.string.servings_format, recipe.servings))
            }

            if (timeInfo.isNotEmpty()) {
                // Add space before time info
                y += textPaint.textSize * 0.5f

                // Draw a light background for time info
                val timeInfoText = timeInfo.joinToString(" | ")
                val timeInfoWidth = textPaint.measureText(timeInfoText)
                val timeInfoPadding = textPaint.textSize * 0.8f

                // Draw background
                val timeInfoBgPaint = Paint().apply {
                    color = Color.argb(30, 0, 0, 0) // Very light gray background
                    style = Paint.Style.FILL
                }

                canvas.drawRoundRect(
                    horizontalMargin - timeInfoPadding,
                    y - textPaint.textSize * 0.7f,
                    horizontalMargin + timeInfoWidth + timeInfoPadding,
                    y + textPaint.textSize * 1.2f,
                    8f * scaleFactor,
                    8f * scaleFactor,
                    timeInfoBgPaint,
                )

                // Draw text
                canvas.drawText(timeInfoText, horizontalMargin, y + textPaint.textSize * 0.3f, textPaint)
                y += textPaint.textSize * 2f // More space after time info
            }

            // Draw ingredients section with more spacing
            y += subtitlePaint.textSize * 0.8f // Space before section header
            canvas.drawText(context.getString(R.string.ingredients_header), horizontalMargin, y, subtitlePaint)
            y += subtitlePaint.textSize * 1.5f // More space after header

            for (ingredient in recipe.ingredients) {
                if (ingredient.trim().isNotEmpty()) {
                    val ingredientLines = splitTextIntoLines(
                        "• $ingredient",
                        textPaint,
                        pageWidth - 2 * horizontalMargin,
                    )

                    for (line in ingredientLines) {
                        canvas.drawText(line, horizontalMargin, y, textPaint)
                        y += textPaint.textSize * 1.2f
                    }
                }
            }

            y += textPaint.textSize // More space after ingredients

            // Draw instructions section with more spacing
            y += subtitlePaint.textSize * 0.8f // Space before section header
            canvas.drawText(context.getString(R.string.instructions_header), horizontalMargin, y, subtitlePaint)
            y += subtitlePaint.textSize * 1.5f // More space after header

            for (i in recipe.instructions.indices) {
                if (recipe.instructions[i].trim().isNotEmpty()) {
                    val instructionLines = splitTextIntoLines(
                        "${i + 1}. ${recipe.instructions[i]}",
                        textPaint,
                        pageWidth - 2 * horizontalMargin,
                    )

                    for (line in instructionLines) {
                        canvas.drawText(line, horizontalMargin, y, textPaint)
                        y += textPaint.textSize * 1.2f
                    }

                    y += textPaint.textSize * 0.3f
                }
            }

            // Draw Additional Notes section if available
            if (recipe.notes.isNotEmpty()) {
                y += subtitlePaint.textSize * 0.8f // Space before section header
                canvas.drawText("ADDITIONAL NOTES", horizontalMargin, y, subtitlePaint)
                y += subtitlePaint.textSize * 1.5f // More space after header

                val notesLines = splitTextIntoLines(
                    recipe.notes,
                    textPaint,
                    pageWidth - 2 * horizontalMargin,
                )

                for (line in notesLines) {
                    canvas.drawText(line, horizontalMargin, y, textPaint)
                    y += textPaint.textSize * 1.2f
                }

                y += textPaint.textSize // More space after notes
            }

            // Draw footer with app name and date
            val footerText = context.getString(
                R.string.printed_from_app,
                context.getString(R.string.app_name),
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
            )
            val footerPaint = Paint().apply {
                color = Color.BLACK // Changed from Color.GRAY to Color.BLACK for better visibility
                textSize = 14f * scaleFactor // Increased from 12f to 14f
                isFakeBoldText = true // Added bold styling
            }
            val footerWidth = footerPaint.measureText(footerText)
            val footerX = (pageWidth - footerWidth) / 2
            val footerY = pageHeight - verticalMargin

            canvas.drawText(footerText, footerX, footerY, footerPaint)

            Log.d(TAG, "Recipe content drawn successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error in drawRecipe: ${e.message}", e)
        }
    }

    private fun calculateInSampleSize(
        options: android.graphics.BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int,
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun splitTextIntoLines(text: String, paint: Paint, maxWidth: Float): List<String> {
        val lines = mutableListOf<String>()
        val words = text.split(" ")
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val testWidth = paint.measureText(testLine)

            if (testWidth <= maxWidth) {
                currentLine = testLine
            } else {
                lines.add(currentLine)
                currentLine = word
            }
        }

        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        return lines
    }

    /**
     * Clean up resources when adapter is no longer needed
     */
    fun cleanup() {
        try {
            // Recycle bitmap to free memory
            recipeBitmap?.recycle()
            recipeBitmap = null

            // Close PDF document if still open
            pdfDocument?.close()
            pdfDocument = null

            Log.d(TAG, "Resources cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up resources: ${e.message}", e)
        }
    }
}
