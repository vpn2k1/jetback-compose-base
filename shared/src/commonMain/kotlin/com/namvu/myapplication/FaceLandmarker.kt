package com.namvu.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

data class FaceLandmarkPoint(
    val x: Float,
    val y: Float,
    val z: Float,
)

data class FaceLandmarkConnection(
    val start: Int,
    val end: Int,
)

data class FaceTransformationMatrix(
    val values: List<Float>,
) {
    init {
        require(values.size == MatrixValueCount) {
            "Face transformation matrix must contain $MatrixValueCount values."
        }
    }

    operator fun get(row: Int, column: Int): Float = values[row * MatrixSize + column]

    companion object {
        const val MatrixSize = 4
        const val MatrixValueCount = MatrixSize * MatrixSize
    }
}

data class FaceBlendShape(
    val index: Int,
    val categoryName: String,
    val displayName: String,
    val score: Float,
)

data class FaceTrackingResult(
    val imageWidth: Int,
    val imageHeight: Int,
    val faces: List<List<FaceLandmarkPoint>>,
    val blendShapes: List<List<FaceBlendShape>>,
    val facialTransformationMatrixes: List<FaceTransformationMatrix>,
    val connections: List<FaceLandmarkConnection>,
    val timestampMs: Long,
)

data class FaceLandmarkerOutput(
    val imageWidth: Int,
    val imageHeight: Int,
    val faces: List<List<FaceLandmarkPoint>>,
    val blendShapes: List<List<FaceBlendShape>> = emptyList(),
    val facialTransformationMatrixes: List<FaceTransformationMatrix>,
    val connections: List<FaceLandmarkConnection>,
)

data class FaceVideoResult(
    val durationMs: Long,
    val analyzedFrames: Int,
    val previewFrameBytes: ByteArray?,
    val trackingResult: FaceTrackingResult?,
)

interface StaticFaceLandmarker {
    suspend fun detect(imageBytes: ByteArray): FaceLandmarkerOutput?
}

interface FaceVideoLandmarker {
    suspend fun detectVideo(videoBytes: ByteArray): FaceVideoResult?
}

interface FaceTrackerEngine {
    val trackingResult: State<FaceTrackingResult?>
    val isRunning: State<Boolean>
    val error: State<String?>

    fun start()
    fun stop()
}

@Composable
expect fun rememberStaticFaceLandmarker(): StaticFaceLandmarker

@Composable
expect fun rememberFaceVideoLandmarker(): FaceVideoLandmarker

@Composable
expect fun rememberFaceTrackerEngine(): FaceTrackerEngine
