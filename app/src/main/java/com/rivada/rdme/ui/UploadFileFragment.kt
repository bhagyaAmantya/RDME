package com.rivada.rdme.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rivada.rdme.R
import com.rivada.rdme.utils.AppConstants
import com.rivada.rdme.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_upload.*


@AndroidEntryPoint
class UploadFileFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //viewModel.updateConfigDialog(false)
        btn_upload.setOnClickListener(View.OnClickListener {
            val pdfIntent = Intent(Intent.ACTION_GET_CONTENT)
            pdfIntent.type = "application/json"
            pdfIntent.addCategory(Intent.CATEGORY_OPENABLE)
            activity?.startActivityForResult(pdfIntent, AppConstants.SELECT_FILE)
        })
        viewModel.nPayLoad.observe(viewLifecycleOwner) {
            json_text.text = "Video URL:" +
                    " ${it?.payload?.video?.url}"
        }

    }


}