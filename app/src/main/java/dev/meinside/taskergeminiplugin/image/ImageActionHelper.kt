package dev.meinside.taskergeminiplugin.image

import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.output.TaskerOutputsForConfig

class ImageActionHelper(config: TaskerPluginConfig<ImageInput>) : TaskerPluginConfigHelper<ImageInput, ImageOutput, ImageRunner>(config) {
    override val runnerClass = ImageRunner::class.java
    override val inputClass = ImageInput::class.java
    override val outputClass = ImageOutput::class.java

    override fun addOutputs(input: TaskerInput<ImageInput>, output: TaskerOutputsForConfig) {
        super.addOutputs(input, output)
    }
}