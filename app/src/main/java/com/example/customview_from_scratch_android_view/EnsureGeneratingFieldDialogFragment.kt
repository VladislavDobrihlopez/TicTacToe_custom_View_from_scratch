package com.example.customview_from_scratch_android_view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class EnsureGeneratingFieldDialogFragment : DialogFragment() {
    var callback: ((Boolean) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.confirmation))
                .setMessage(getString(R.string.generate_new_game_field))
                .setIcon(android.R.drawable.ic_menu_always_landscape_portrait)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.confirm)) { _, _ -> callback?.invoke(true) }
                .setNegativeButton(
                    getString(R.string.dismiss)
                ) { _, _ -> callback?.invoke(false) }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}