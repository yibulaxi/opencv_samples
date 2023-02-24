package com.cool.yfc.tools.mediaselect.engine.video

/**
 *
 * @author yfc
 * @since 2022/07/29 13:56
 * @version V1.0
 */
object VideoEngine {
    val exoPlayerEngine: ExoPlayerEngine = lazy { ExoPlayerEngine.instance }.value
    val ijkPlayerEngine: IjkPlayerEngine = lazy { IjkPlayerEngine.instance }.value
}