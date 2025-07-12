package dev.meinside.taskergeminiplugin.image

import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputObject
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputVariable

@TaskerOutputObject
class ImageOutput (
    @get:TaskerOutputVariable(VAR_GENERATED_TEXT, labelResIdName = VAR_GENERATED_TEXT) var generated: String = ""
){
    companion object {
        const val VAR_GENERATED_TEXT = "gemini_image_text"
    }
}