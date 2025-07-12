package dev.meinside.taskergeminiplugin.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerAction
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import dev.meinside.taskergeminiplugin.R
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

class ImageRunner : TaskerPluginRunnerAction<ImageInput, ImageOutput>() {
    override fun run(context: Context, input: TaskerInput<ImageInput>): TaskerPluginResult<ImageOutput> {
        var generated: String? = null

        if (input.regular.model.trim() == "") {
            generated = context.getString(R.string.error_message_no_model)
        } else if (input.regular.apiKey.trim() == "") {
            generated = context.getString(R.string.error_message_no_api_key)
        } else if (input.regular.prompt.trim() == "") {
            generated = context.getString(R.string.error_message_no_prompt)
        } else if (input.regular.imageUri.trim() == "") {
            generated = "No image provided"
        } else {
            try {
                val bitmap = loadBitmapFromPath(input.regular.imageUri)
                if (bitmap != null) {
                    runBlocking {
                        supervisorScope {
                            val model = GenerativeModel(modelName = input.regular.model, apiKey = input.regular.apiKey)
                            
                            val inputContent = content {
                                image(bitmap)
                                text(input.regular.prompt)
                            }
                            
                            val res = model.generateContent(inputContent)

                            res.promptFeedback?.blockReason?.let {
                                generated = context.getString(R.string.error_message_blocked).format(it.name)
                            }

                            if (res.candidates.isNotEmpty() &&
                                res.candidates.first().content.parts.isNotEmpty() &&
                                generated.isNullOrBlank()
                            ) {
                                generated = res.candidates.first().content.parts.firstOrNull()?.let { part ->
                                    when (part) {
                                        is com.google.ai.client.generativeai.type.TextPart -> part.text
                                        else -> "Unexpected response format"
                                    }
                                }
                            }
                        }
                    }
                } else {
                    generated = "Failed to load image"
                }
            } catch (e: Exception) {
                generated = "Error processing image: ${e.message}"
            }
        }

        generated = generated ?: context.getString(R.string.error_message_unknown)

        return TaskerPluginResultSucess(ImageOutput(generated!!))
    }

    private fun loadBitmapFromPath(imagePath: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(imagePath)
        } catch (e: Exception) {
            null
        }
    }
}