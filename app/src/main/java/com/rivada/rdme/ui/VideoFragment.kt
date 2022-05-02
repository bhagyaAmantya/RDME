package com.rivada.rdme.ui


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.rivada.rdme.R
import com.rivada.rdme.model.Cell
import com.rivada.rdme.model.PayLoadModel
import com.rivada.rdme.model.Payload
import com.rivada.rdme.utils.AppConstants
import com.rivada.rdme.utils.AppConstants.Companion.KEY_CELL_LIST
import com.rivada.rdme.utils.getJsonDataFromAsset
import com.rivada.rdme.utils.readJSON
import com.rivada.rdme.utils.signalStrengthCalculation
import com.rivada.rdme.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_video.*
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader


@AndroidEntryPoint
class VideoFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var player: ExoPlayer? = null
    private lateinit var session: AppConstants
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private  var nRCellId:String? = null
    private var cellsList: List<Cell>? = null
    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(requireActivity(), "exoplayer-sample")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        session = AppConstants(requireActivity())
        if (session.isUpdate()) {
            cellsList = session.getArrayList(KEY_CELL_LIST)
            if (cellsList != null) {
                for (cells in cellsList!!) {
                    if (cells.id == nRCellId) {
                        setUpCellData(cells.cellname,
                            cells.color)
                    }
                }
                setUpRawData(
                    session.getVideo(),
                    session.getURL()
                )
            }
        }
        else{
            updateConfigFileAlert(view)
        }

        viewModel.nConfigDialog.observe(viewLifecycleOwner){
            if (it == false)
                updateConfigFileAlert(view)
        }
        viewModel.cId.observe(viewLifecycleOwner) {
            if (!it.cid.equals(0)) {
                nRCellId = it.cid
                if (cellsList != null) {
                    for (cells in cellsList!!) {
                        if (cells.id == nRCellId) {
                            setUpCellData(
                                cells.cellname,
                                cells.color
                            )
                        }
                    }
                }
                internet_status.text = "${it.type} Cell ID: ${it.cid}"
            }
        }

       /* viewModel.nSignalData.observe(viewLifecycleOwner) {
         signal_values.text = "Rsrp: ${it.getSsRsrp}dbm \nRsrq: ${it.getSsRsrq}db  \nSinr: ${it.getSsSinr}"
           // val cell = signalStrengthCalculation(it)
        }*/
        viewModel.nSignalStrength.observe(viewLifecycleOwner){
            signal_status.text= it
        }
        viewModel.nColorCode.observe(viewLifecycleOwner){
            signal_status.setTextColor(Color.parseColor(it))

        }

        viewModel.nPayLoad.observe(viewLifecycleOwner) {
          for(cells in it.payload.cells){
              if (cells.id == nRCellId){
                  setUpRawData(
                      it?.payload?.video?.showvideo,
                      it?.payload?.video?.url
                  )
                  setUpCellData(cells.cellname,
                      cells.color)
              }
          }

        }
    }

    private fun setUpRawData(
        showVideo: String?,
        videoUrl: String?
    ) {
        if (showVideo == "true") {
            val uri = Uri.parse(videoUrl)
            if (videoUrl?.contains("rtsp://") == true || uri.lastPathSegment?.contains("webm") == true) {
                initializeVideoPlayer(videoUrl)
            } else {
                initializeExoPlayer(videoUrl)
            }
        }
    }

    private fun setUpCellData(
        cellName: String?,
        color: String?
    ) {
        cellID.text = cellName
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
        val onInfoToPlayStateListener: MediaPlayer.OnInfoListener =
            MediaPlayer.OnInfoListener { mp, what, extra ->
                if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                    my_spinner.visibility = View.GONE
                }
                if (MediaPlayer.MEDIA_INFO_BUFFERING_START == what) {
                    my_spinner.visibility = View.VISIBLE
                }
                if (MediaPlayer.MEDIA_INFO_BUFFERING_END == what) {
                    my_spinner.visibility = View.GONE
                }
                false
            }
        videoView.start()
        videoView.setOnInfoListener(onInfoToPlayStateListener)
    }

    private fun initializeExoPlayer(videoJsonUrl: String?) {
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

   override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
        player?.seekTo(currentWindow, playbackPosition)
        Log.i("resume","again resume")
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
    private fun updateConfigFileAlert(view:View) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(R.string.title_config)
            .setCancelable(false)
            .setPositiveButton(R.string.ok) { dialog, id ->dialog.dismiss()}
              //  Navigation.findNavController(view).navigate(R.id.action_videoFragment_to_settingFragment)}
        val alert = dialogBuilder.create()
        alert.setTitle(R.string.add)
        alert.show()
    }
}


