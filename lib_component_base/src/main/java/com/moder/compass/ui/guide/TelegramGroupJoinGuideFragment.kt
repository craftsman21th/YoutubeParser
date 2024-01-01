package com.moder.compass.ui.guide

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.DialogFragment
import com.moder.compass.component.base.R
import com.mars.kotlin.extension.e
import kotlinx.android.synthetic.main.fragment_telegram_guide.*

private const val TELEGRAM_PACKAGE_NAME = "org.telegram.messenger"
private const val TELEGRAM_PACKAGE_WEB_NAME = "org.telegram.messenger.web"
internal const val KEY_TELEGRAM_GROUP_LINK = "key_telegram_group_link"

internal class TelegramGroupJoinGuideFragment : DialogFragment() {
    private val telegramGroupLink: String? by lazy {
        arguments?.getString(KEY_TELEGRAM_GROUP_LINK)
    }
    private val telegramPackages = listOf(
        TELEGRAM_PACKAGE_NAME,
        TELEGRAM_PACKAGE_WEB_NAME
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.ModerDialogTheme)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            var params = attributes
            if (params == null) {
                params = WindowManager.LayoutParams()
            }
            setGravity(Gravity.BOTTOM)
            setWindowAnimations(R.style.anim_dialog_slide_from_bottom)
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            attributes = params
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_telegram_guide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        close.setOnClickListener {
            dismiss()
        }
        join.setOnClickListener {
            if (!TextUtils.isEmpty(telegramGroupLink)) {
                telegramPackages.forEach {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(telegramGroupLink)).apply {
                            setPackage(it)
                        })
                        dismiss()
                        return@setOnClickListener
                    } catch (e: ActivityNotFoundException) {
                        e.e()
                    }
                }
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(telegramGroupLink)))
                dismiss()
            }
        }
    }
}