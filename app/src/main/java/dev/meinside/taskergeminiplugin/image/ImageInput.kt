package dev.meinside.taskergeminiplugin.image

import com.joaomgcd.taskerpluginlibrary.input.TaskerInputField
import com.joaomgcd.taskerpluginlibrary.input.TaskerInputRoot

@TaskerInputRoot
class ImageInput @JvmOverloads constructor(
    @field:TaskerInputField(VAR_MODEL, labelResIdName = VAR_MODEL) var model: String = "",
    @field:TaskerInputField(VAR_API_KEY, labelResIdName = VAR_API_KEY) var apiKey: String = "",
    @field:TaskerInputField(VAR_PROMPT, labelResIdName = VAR_PROMPT) var prompt: String = "",
    @field:TaskerInputField(VAR_IMAGE_URI, labelResIdName = VAR_IMAGE_URI) var imageUri: String = "",
){
    companion object {
        const val VAR_MODEL = "gemini_model"
        const val VAR_API_KEY = "gemini_api_key"
        const val VAR_PROMPT = "gemini_prompt"
        const val VAR_IMAGE_URI = "gemini_image_uri"
    }
}