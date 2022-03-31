package com.rivada.rdme.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rivada.rdme.R
import com.rivada.rdme.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_video.*

class VideoFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.cId.observe(viewLifecycleOwner) {
          if(!it.cid.equals(0)){
                internet_status.text = "${it.type} Cell ID:${it.cid}"
          }
        }
    }

}