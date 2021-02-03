package com.heycode.mlappone2x_1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class MainActivity : AppCompatActivity() {
    lateinit var interpreter:Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        try {
            interpreter = Interpreter(loadModelFile(),null)

        }catch (e:Exception){
            println(e.stackTrace)
        }
        val predictButton:Button = findViewById(R.id.button_predict)
        val userInput: EditText = findViewById(R.id.user_input)
        val resultText: TextView = findViewById(R.id.result_text)

        predictButton.setOnClickListener {
            val resultFloat = doInference(userInput.text.toString())
            resultText.text = "Result:\nY = $resultFloat"
        }
    }

    private fun doInference(`val`: String): Float {
        val input = FloatArray(1)
        input[0] = `val`.toFloat()
        val output = Array(1) { FloatArray(1) }
        interpreter.run(input, output)
        return output[0][0]
    }


    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer? {
        val assetFileDescriptor = this.assets.openFd("linear.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = fileInputStream.getChannel()
        val startOffset = assetFileDescriptor.startOffset
        val length = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
    }
}