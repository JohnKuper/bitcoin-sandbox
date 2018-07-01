package com.kaizendeveloper.bitcoinsandbox.ui.view

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.kaizendeveloper.bitcoinsandbox.R

class ProgressFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_progress_dialog, container, false)

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            isCancelable = false

            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            window.setLayout(width, height)
        }
    }

    companion object {

        private val TAG = ProgressFragment::class.java.name

        fun show(fragmentManager: FragmentManager) {
            val fragment = fragmentManager.findFragmentByTag(TAG) as? ProgressFragment
            if (fragment == null) {
                ProgressFragment()
                    .showNow(fragmentManager, TAG)
            }
        }

        fun hide(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as? ProgressFragment)?.dismissAllowingStateLoss()
        }
    }
}
