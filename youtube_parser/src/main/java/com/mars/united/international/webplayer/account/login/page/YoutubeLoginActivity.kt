package com.mars.united.international.webplayer.account.login.page

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.mars.united.international.webplayer.R
import com.mars.united.international.webplayer.account.login.state.CALLBACK_ID
import com.mars.united.international.webplayer.account.login.state.PAGE_TARGET
import com.mars.united.international.webplayer.account.login.state.PageTarget
import com.mars.united.international.webplayer.databinding.ActivityYoutubeLoginBinding

/**
 * Youtube登录页
 */
class YoutubeLoginActivity : AppCompatActivity() {

    companion object {

        private const val TAG: String = "YoutubeLoginActivity"

    }

    private var binding: ActivityYoutubeLoginBinding? = null

    private val pageTarget: PageTarget by lazy {
        (intent.getSerializableExtra(PAGE_TARGET) as? PageTarget) ?: PageTarget.ToLogin
    }

    private val callbackId: Long by lazy {
        intent.getLongExtra(CALLBACK_ID, 0L)
    }

    private val youtubeLoginFragment: YoutubeLoginFragment by lazy {
        YoutubeLoginFragment().apply {
            arguments = Bundle().apply {
                putSerializable(PAGE_TARGET, pageTarget)
                putLong(CALLBACK_ID, callbackId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityYoutubeLoginBinding.inflate(
                LayoutInflater.from(this),
                null,
                true
            ).apply {
                binding = this
            }.root
        )
        loadFragment()
    }

    private fun loadFragment() {
        binding?.root
        supportFragmentManager.beginTransaction().apply {
            add(R.id.youtube_login_activity_root, youtubeLoginFragment)
        }.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        if (youtubeLoginFragment.onBackPressed()) return
        super.onBackPressed()
    }

}