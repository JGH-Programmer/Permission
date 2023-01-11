package git.myapplication.permission

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import git.myapplication.permission.databinding.ActivityAudioRecordBinding
import kotlinx.android.synthetic.main.activity_audio_record.*
import java.util.*

class audioRecordActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAudioRecordBinding.inflate(layoutInflater)
    }

    private var speechRecognizer: SpeechRecognizer? = null

    private  fun startSTT() {
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(recognitionListener())
            startListening(speechRecognizerIntent)
        }

    }

    private fun recognitionListener() = object : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?){
            Toast.makeText(this@audioRecordActivity, "음성인식 시작", Toast.LENGTH_SHORT).show()
            button.setImageResource(R.drawable.ic_mics)
        }




        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}
        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onBeginningOfSpeech() {
            textView.setText("Listening")
        }

        override fun onEndOfSpeech() {
            Log.d("end","end")
        }

        override fun onError(error: Int) {
            when(error) {
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Toast.makeText(this@audioRecordActivity, "퍼미션 없음", Toast.LENGTH_SHORT).show()
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> Log.d("end","end")
            }
        }



        override fun onResults(results: Bundle) {
            Toast.makeText(this@audioRecordActivity, "음성인식 종료", Toast.LENGTH_SHORT).show()
            button.setImageResource(R.drawable.ic_mic_offs)
            textView.text = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)!![0]
            Log.d("end","end")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.button.setOnClickListener {
            startSTT()

        }


    }
}