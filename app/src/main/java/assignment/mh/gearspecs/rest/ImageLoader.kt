package assignment.mh.gearspecs.rest

import android.graphics.BitmapFactory
import androidx.collection.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request

object ImageLoader {
    private val httpClient : OkHttpClient
    private val requests = mutableMapOf<Callback, Job>()
    private val imageCache = ImageCache(5 * 1024 * 1024)

    init {
        httpClient = OkHttpClient.Builder().build()
    }

    /** If the image is in cache, it will return it immediately,
     * otherwise it will return null and the result will be posted later via the Callback*/
    fun get(url: String, callback: Callback? = null) : ImageBitmap? {
        val image = imageCache.get(url)
        if (image != null) {
            return image
        }
        if (callback == null) {
            return null
        }

        val job = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
            if (isActive) {
                var image : ImageBitmap? = null
                val request = Request.Builder().get().url(url).build()
                val response = httpClient.newCall(request).execute()
                val code = response.code()
                val byteStream = response.body()?.byteStream()

                if (isActive && byteStream != null) {
                    image = BitmapFactory.decodeStream(byteStream).asImageBitmap()
                    // The imageCache is thread safe, no need to work on synchronization
                    imageCache.put(url, image)
                }
                byteStream?.close()
                response.close()

                if (isActive) {
                    CoroutineScope(Dispatchers.Main).launch {
                        //Done on Main Thread, no need to synchronize
                        requests.remove(callback)
                        callback.onResult(url, image, code)
                    }
                }
            }
        }

        requests.put(callback, job)
        job.start()
        return null
    }

    fun cancel(callback: Callback) {
        requests.remove(callback)?.cancel()
    }

    private class ImageCache(maxSizeInBytes:Int) : LruCache<String, ImageBitmap>(maxSizeInBytes) {

        override fun sizeOf(key: String, value: ImageBitmap): Int {
            return value.height * value.width * 4 //size in bytes of the image
        }
    }

    interface Callback {
        fun onResult(url:String, image:ImageBitmap?, httpCode:Int)
    }
}