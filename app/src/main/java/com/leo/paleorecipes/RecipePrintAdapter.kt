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
import com.bumptech.glide.Glide
import com.leo.paleorecipes.data.Recipe
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class RecipePrintAdapter(private val context: Context, private val recipe: Recipe) : PrintDocumentAdapter() {
    private val TAG = "RecipePrintAdapter"
    private var pdfDocument: PdfDocument? = null
    private var recipeBitmap: Bitmap? = null

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
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

        // Load the image synchronously if available
        if (!recipe.imageUrl.isNullOrEmpty()) {
            try {
                Log.d(TAG, "Loading image from URL: ${recipe.imageUrl}")

                // Use Glide to load the image synchronously with a timeout
                recipeBitmap = try {
                    Glide.with(context)
                        .asBitmap()
                        .load(recipe.imageUrl)
                        .submit(300, 300)
                        .get(5, TimeUnit.SECONDS) // 5 second timeout
                } catch (e: ExecutionException) {
                    Log.e(TAG, "Error executing image load: ${e.message}")
                    null
                } catch (e: InterruptedException) {
                    Log.e(TAG, "Image loading interrupted: ${e.message}")
                    null
                } catch (e: TimeoutException) {
                    Log.e(TAG, "Image loading timed out")
                    null
                } catch (e: Exception) {
                    Log.e(TAG, "Unexpected error loading image: ${e.message}")
                    null
                }

                if (recipeBitmap != null) {
                    Log.d(TAG, "Image loaded successfully: ${recipeBitmap!!.width}x${recipeBitmap!!.height}")
                } else {
                    Log.d(TAG, "Failed to load image, continuing without it")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error preparing image: ${e.message}")
                // Continue without the image
                recipeBitmap = null
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
        callback: WriteResultCallback
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
                (11f * 72).toInt(),  // 11 inches in points
                0                    // Page index (0-based)
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

            // Draw title
            val titleWidth = titlePaint.measureText(recipe.title)
            val titleX = (pageWidth - titleWidth) / 2
            canvas.drawText(recipe.title, titleX, y, titlePaint)
            y += titlePaint.textSize * 1.5f

            // Draw separator line
            canvas.drawLine(horizontalMargin, y, pageWidth - horizontalMargin, y, linePaint)
            y += subtitlePaint.textSize

            // Draw image if available
            if (recipeBitmap != null) {
                try {
                    Log.d(TAG, "Drawing image to PDF")

                    // Calculate image dimensions
                    val maxImageWidth = pageWidth - (2 * horizontalMargin)
                    val maxImageHeight = pageHeight * 0.25f

                    val imageWidth: Float
                    val imageHeight: Float

                    val bitmapRatio = recipeBitmap!!.width.toFloat() / recipeBitmap!!.height.toFloat()

                    if (bitmapRatio > 1) {
                        // Landscape image
                        imageWidth = maxImageWidth
                        imageHeight = imageWidth / bitmapRatio
                    } else {
                        // Portrait image
                        imageHeight = maxImageHeight
                        imageWidth = imageHeight * bitmapRatio
                    }

                    // Center the image horizontally
                    val imageX = (pageWidth - imageWidth) / 2

                    // Draw the image
                    canvas.drawBitmap(
                        recipeBitmap!!,
                        null,
                        android.graphics.RectF(imageX, y, imageX + imageWidth, y + imageHeight),
                        null
                    )

                    // Update y position
                    y += imageHeight + (subtitlePaint.textSize * 1.5f)

                    Log.d(TAG, "Image drawn successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Error drawing image: ${e.message}", e)
                    // Continue without the image
                }
            }

            // Draw description if available
            if (recipe.description.isNotEmpty()) {
                val descriptionLines = splitTextIntoLines(
                    recipe.description,
                    textPaint,
                    pageWidth - 2 * horizontalMargin
                )

                for (line in descriptionLines) {
                    canvas.drawText(line, horizontalMargin, y, textPaint)
                    y += textPaint.textSize * 1.2f
                }

                y += textPaint.textSize * 0.5f
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
                val timeInfoText = timeInfo.joinToString(" | ")
                canvas.drawText(timeInfoText, horizontalMargin, y, textPaint)
                y += textPaint.textSize * 1.5f
            }

            // Draw ingredients
            canvas.drawText(context.getString(R.string.ingredients_header), horizontalMargin, y, subtitlePaint)
            y += subtitlePaint.textSize

            for (ingredient in recipe.ingredients) {
                if (ingredient.trim().isNotEmpty()) {
                    val ingredientLines = splitTextIntoLines(
                        "• $ingredient",
                        textPaint,
                        pageWidth - 2 * horizontalMargin
                    )

                    for (line in ingredientLines) {
                        canvas.drawText(line, horizontalMargin, y, textPaint)
                        y += textPaint.textSize * 1.2f
                    }
                }
            }

            y += textPaint.textSize * 0.5f

            // Draw instructions
            canvas.drawText(context.getString(R.string.instructions_header), horizontalMargin, y, subtitlePaint)
            y += subtitlePaint.textSize

            for (i in recipe.instructions.indices) {
                if (recipe.instructions[i].trim().isNotEmpty()) {
                    val instructionLines = splitTextIntoLines(
                        "${i + 1}. ${recipe.instructions[i]}",
                        textPaint,
                        pageWidth - 2 * horizontalMargin
                    )

                    for (line in instructionLines) {
                        canvas.drawText(line, horizontalMargin, y, textPaint)
                        y += textPaint.textSize * 1.2f
                    }

                    y += textPaint.textSize * 0.3f
                }
            }

            // Draw footer with app name and date
            val footerText = context.getString(
                R.string.printed_from_app,
                context.getString(R.string.app_name),
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            )
            val footerPaint = Paint().apply {
                color = Color.GRAY
                textSize = 12f * scaleFactor
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