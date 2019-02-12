package io.bkraszewski.sample

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPlayer()
    }

    private fun setupPlayer() {
        val source = readTestData()

        val mediaSources = mutableListOf<MediaSource>()
        val dataSourceFactory = DefaultDataSourceFactory(this,
            Util.getUserAgent(this, packageName))
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(source))

        val start = ClippingMediaSource(videoSource, 1140 * 1000, 3140 * 1000)
        mediaSources.add(start)

        val loopingClipped = ClippingMediaSource(videoSource, 3140 * 1000, 34050 * 1000)
        val looped = LoopingMediaSource(loopingClipped)
        mediaSources.add(looped)

        videoView.setKeepContentOnPlayerReset(true)
        val player = ExoPlayerFactory.newSimpleInstance(this)
        videoView.player = player
        player.prepare(ConcatenatingMediaSource(true, false, ShuffleOrder.DefaultShuffleOrder(0), *mediaSources.toTypedArray()))

        player.playWhenReady = true
    }

    private fun readTestData(): String {
        val bufferSize = 1024
        val assetManager = assets
        val assetFiles = assetManager.open("video.mp4")

        val inputStream = assetFiles
        val output = File(cacheDir, "video.mp4")
        val outputStream = FileOutputStream(output)

        try {
            inputStream.copyTo(outputStream, bufferSize)
        } finally {
            inputStream.close()
            outputStream.flush()
            outputStream.close()
        }

        return output.absolutePath

    }

    override fun onDestroy() {
        videoView.player?.release()
        super.onDestroy()
    }
}
