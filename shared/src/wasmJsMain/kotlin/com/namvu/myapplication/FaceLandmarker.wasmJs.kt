package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberStaticFaceLandmarker(): StaticFaceLandmarker = remember {
    UnsupportedStaticFaceLandmarker
}

@Composable
actual fun rememberFaceVideoLandmarker(): FaceVideoLandmarker = remember {
    UnsupportedFaceVideoLandmarker
}

private object UnsupportedStaticFaceLandmarker : StaticFaceLandmarker {
    override suspend fun detect(imageBytes: ByteArray): FaceLandmarkerOutput? = null
}

private object UnsupportedFaceVideoLandmarker : FaceVideoLandmarker {
    override suspend fun detectVideo(videoBytes: ByteArray): FaceVideoResult? = null
}
