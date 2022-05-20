package com.rivada.rdme.ui

import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.rivada.rdme.R
import kotlinx.android.synthetic.main.fragment_upload.*


class ExitFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_upload.visibility = View.GONE
        txt_head.visibility = View.GONE
        switch_btn.visibility = View.GONE
        txt_showVideo.visibility= View.GONE
        txt_showCellId.visibility= View.GONE
        switch_btn_cellId.visibility= View.GONE
        exitApp()

    }

    private fun exitApp() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(R.string.title)
            .setCancelable(false)
            .setPositiveButton(R.string.yes) { dialog, id -> activity?.finish() }
            .setNegativeButton(R.string.no) { dialog, id -> dialog.cancel() }
        val alert = dialogBuilder.create()
        alert.setTitle(R.string.exit)
        alert.show()
    }
}