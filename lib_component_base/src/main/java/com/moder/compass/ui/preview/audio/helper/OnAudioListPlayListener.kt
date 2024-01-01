package com.moder.compass.ui.preview.audio.helper

import com.moder.compass.preview.video.IBaseVideoSource

/**
 * 用于告知外部需要播放哪个音频的监听器
 */
interface OnAudioListPlayListener {
    /**
     * 音频播放事件
     *
     * @param source   正在播放的音频
     * @param position 音频所在列表中的位置
     */
    fun onPlay(source: IBaseVideoSource, position: Int)
}