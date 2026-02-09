package demo.nexa.clinical_transcription_demo

import android.app.Application
import android.util.Log
import com.nexa.sdk.NexaSdk
import demo.nexa.clinical_transcription_demo.nexa.NexaRuntime

/**
 * Application subclass for one-time initialization.
 * Initializes Nexa SDK runtime (shared by ASR and LLM modules).
 */
class PlauApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Nexa SDK once for the entire app lifecycle
        NexaSdk.getInstance().init(this, object : NexaSdk.InitCallback {
            override fun onSuccess() {
                NexaRuntime.markReady()
            }
            
            override fun onFailure(reason: String) {
                Log.e(TAG, "Failed to initialize Nexa SDK: $reason")
                // BrowserStack/emulators may lack OpenCL (libOpenCL.so), causing cpu_gpu plugin registration to fail.
                // Mark as failed so UI can remain usable and explain the limitation.
                NexaRuntime.markFailed(reason)
            }
        })
    }
    
    companion object {
        private const val TAG = "PlauApp"
    }
}
