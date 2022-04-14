package com.rivada.rdme.ui


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MediaController
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.offline.FilteringManifestParser
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.*
import com.rivada.rdme.R
import com.rivada.rdme.model.PayLoadModel
import com.rivada.rdme.utils.AppConstants
import com.rivada.rdme.utils.getJsonDataFromAsset
import com.rivada.rdme.utils.readJSON
import com.rivada.rdme.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_video.*


@AndroidEntryPoint
class VideoFragment : Fragment() {
    companion object {
        private val BANDWIDTH_METER = DefaultBandwidthMeter()
    }

    private val viewModel: MainViewModel by activityViewModels()
    //private var videoJsonUrl: String? = null
    private var player: ExoPlayer? = null
    lateinit var session: AppConstants
    //private var sampleUrl = "https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd"
    //  private var sampleUrl = "https://multiplatform-f.akamaihd.net/i/multi/april11/sintel/sintel-hd_,512x288_450_b,640x360_700_b,768x432_1000_b,1024x576_1400_m,.mp4.csmil/master.m3u8"
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(requireActivity(), "exoplayer-sample")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        session = AppConstants(requireActivity())
        if(session.isUpdate()){
            setUpRawData(session.getvideo(),
                session.getURL(),
                session.getcellname(),
              session.getcolor())
        }else{
            val jsonFileString = getJsonDataFromAsset(requireContext(), "config.json")
            val payLoadModel:PayLoadModel = readJSON(jsonFileString)
            setUpRawData(payLoadModel.payload.video.showvideo,
                payLoadModel.payload.video.url,
                payLoadModel.payload.cells[0].cellname,
                payLoadModel.payload.cells[0].color)
        }
            viewModel.cId.observe(viewLifecycleOwner) {
                if (!it.cid.equals(0)) {
                    internet_status.text = "${it.type} Cell ID:${it.cid}"
                }
            }
            viewModel.nSignalStrength.observe(viewLifecycleOwner) {
                signal_status.text = it

            }
            viewModel.nColorCode.observe(viewLifecycleOwner) {
                signal_status.setTextColor(Color.parseColor(it))
            }

            viewModel.nPayLoad.observe(viewLifecycleOwner) {
                setUpRawData(
                    it?.payload?.video?.showvideo,
                    it?.payload?.video?.url,
                    it.payload.cells[1].cellname,
                    it?.payload?.cells?.get(1)?.color)

            }


    }
    private fun setUpRawData(
        showvideo: String?,
        videoUrl: String?,
        cellname: String?,
        color: String?
    ) {
        if (showvideo == "true") {
            if (videoUrl?.contains("rtsp://") == true) {
                initializeVideoPlayer(videoUrl)
            } else {
                initializeExoPlayer(videoUrl)
            }
        }
        cellID.text = cellname
        layout.setBackgroundColor(Color.parseColor(color))

    }
    private fun initializeVideoPlayer(videoJsonUrl: String?) {
        videoView.visibility = View.VISIBLE
        val uri = Uri.parse(videoJsonUrl)
        val mediaController = MediaController(context)
        mediaController.setAnchorView(videoView)

        videoView.setMediaController(mediaController)
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()
    }

    private fun initializeExoPlayer(videoJsonUrl:String?) {
        video_player_view.visibility = View.VISIBLE
        if (player == null) {
            player = ExoPlayer.Builder(
                requireActivity(),
                DefaultRenderersFactory(requireActivity()),
                DefaultMediaSourceFactory(
                    requireActivity(),
                    DefaultExtractorsFactory()
                )
            ).build()
            /* player = ExoPlayer.Builder( requireActivity(),
                 DefaultRenderersFactory(requireActivity()),
                 DefaultMediaSourceFactory( requireActivity(),
                     DefaultExtractorsFactory()),
                 DefaultTrackSelector(),
                 DefaultLoadControl(),
                 DefaultBandwidthMeter(),
                 DefaultAnalyticsCollector(Clock.DEFAULT)
             ).build()*/
            video_player_view?.player = player
            player!!.playWhenReady = playWhenReady
            player!!.seekTo(currentWindow, playbackPosition)

        }
        val mediaSource = buildMediaSource(Uri.parse(videoJsonUrl))
        player!!.prepare(mediaSource, true, false)


    }


    private fun buildMediaSource(uri: Uri): MediaSource {
        return when {
            uri.lastPathSegment?.contains("mp3") == true || uri.lastPathSegment?.contains("mp4") == true -> {
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri))
            }
            uri.lastPathSegment?.contains("m3u8") == true -> {
                HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri))
            }
            else -> {
                /* val dashChunkSourceFactory = DefaultDashChunkSource.Factory(
                     DefaultDataSourceFactory("ua", BANDWIDTH_METER))
                 val manifestDataSourceFactory = DefaultDataSourceFactory(dataSourceFactory)
                 return DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(
                     MediaItem.fromUri(uri))*/
                return DashMediaSource.Factory(
                    DefaultDashChunkSource.Factory(dataSourceFactory, 1),
                    dataSourceFactory
                )
                    .createMediaSource(MediaItem.fromUri(uri))
            }
        }
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player!!.release()
            player = null
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

}


