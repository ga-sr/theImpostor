package com.impostor.jogo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * O jogo vive em https://ga-sr.github.io/theImpostor/.
 * Ao abrir, o app baixa a versão mais recente e guarda uma cópia.
 * Sem internet, usa a última cópia salva. Na primeira vez sem internet,
 * usa a cópia que veio dentro do APK.
 */
class MainActivity : Activity() {

    private lateinit var web: WebView
    private val cacheFile by lazy { File(filesDir, "index.html") }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.statusBarColor = BG
        window.navigationBarColor = BG

        web = WebView(this).apply {
            setBackgroundColor(BG)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()
            // O jogo chama AndroidHaptics.vibrate("40") ou ("300,100,300").
            addJavascriptInterface(Haptics(this@MainActivity), "AndroidHaptics")
        }
        setContentView(web)
        loadGame()
    }

    private fun loadGame() {
        thread {
            val html = download() ?: readCache() ?: readBundled()
            runOnUiThread {
                // Mesma origem sempre, para os jogadores salvos não se perderem.
                web.loadDataWithBaseURL(SITE_URL, html, "text/html", "UTF-8", null)
            }
        }
    }

    private fun download(): String? = try {
        val url = URL("${SITE_URL}index.html?t=${System.currentTimeMillis()}")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
            useCaches = false
        }
        try {
            if (conn.responseCode != 200) null
            else conn.inputStream.bufferedReader(Charsets.UTF_8).readText()
                .takeIf { it.contains("const WORDS") }
                ?.also { cacheFile.writeText(it, Charsets.UTF_8) }
        } finally {
            conn.disconnect()
        }
    } catch (e: Exception) {
        null
    }

    private fun readCache(): String? =
        if (cacheFile.exists()) cacheFile.readText(Charsets.UTF_8) else null

    private fun readBundled(): String =
        assets.open("index.html").bufferedReader(Charsets.UTF_8).use { it.readText() }

    /** Vibração nativa. O WebView do Android não implementa navigator.vibrate. */
    private class Haptics(context: Context) {
        private val vibrator: Vibrator? =
            if (Build.VERSION.SDK_INT >= 31) {
                context.getSystemService(VibratorManager::class.java)?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

        @JavascriptInterface
        fun vibrate(pattern: String) {
            val v = vibrator ?: return
            if (!v.hasVibrator()) return
            val ms = pattern.split(",").mapNotNull { it.trim().toLongOrNull() }
                .map { it.coerceIn(0, 2000) }
            if (ms.isEmpty()) return
            if (Build.VERSION.SDK_INT >= 26) {
                val effect = if (ms.size == 1) {
                    VibrationEffect.createOneShot(ms[0], VibrationEffect.DEFAULT_AMPLITUDE)
                } else {
                    // Padrão web: [vibra, pausa, vibra]. Android: [espera, vibra, pausa, ...].
                    VibrationEffect.createWaveform((listOf(0L) + ms).toLongArray(), -1)
                }
                v.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                if (ms.size == 1) v.vibrate(ms[0])
                else v.vibrate((listOf(0L) + ms).toLongArray(), -1)
            }
        }
    }

    companion object {
        private const val SITE_URL = "https://ga-sr.github.io/theImpostor/"
        private const val TIMEOUT_MS = 4000
        private val BG = Color.parseColor("#0d0d10")
    }
}
