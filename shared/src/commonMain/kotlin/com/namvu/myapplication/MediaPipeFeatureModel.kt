package com.namvu.myapplication

import androidx.compose.ui.graphics.Color

enum class MediaPipeFeature(
    val label: String,
) {
    Background("Background"),
    FaceFilter("Face Filter"),
    ObjectDetection("Object Detection"),
    PoseDetection("Pose Detection"),
    HandTracking("Hand Tracking"),
    Export("Export"),
}

enum class MediaPipeFeatureCategory(
    val label: String,
) {
    Vision("Vision"),
    Face("Face"),
    Body("Body"),
    Object("Object"),
    Utility("Utility"),
}

enum class MediaPipeFeatureStatus(
    val label: String,
) {
    Planned("Planned"),
    ReadyForDesign("Design"),
    ReadyForEngine("Engine"),
}

data class MediaPipeFeatureDefinition(
    val id: String,
    val title: String,
    val description: String,
    val category: MediaPipeFeatureCategory,
    val status: MediaPipeFeatureStatus,
    val inputType: String,
    val outputType: String,
    val accentColor: Color,
)

data class MediaPipeFeatureListState(
    val selectedFeatureId: String = DefaultMediaPipeFeatures.first().id,
    val openedFeatureId: String? = null,
    val features: List<MediaPipeFeatureDefinition> = DefaultMediaPipeFeatures,
) {
    val selectedFeature: MediaPipeFeatureDefinition
        get() = features.firstOrNull { it.id == selectedFeatureId } ?: features.first()

    val openedFeature: MediaPipeFeatureDefinition?
        get() = openedFeatureId?.let { id -> features.firstOrNull { it.id == id } }
}

val DefaultMediaPipeFeatures = listOf(
    MediaPipeFeatureDefinition(
        id = "image_segmentation",
        title = "Tach nguoi / thay nen",
        description = "Dung Image Segmenter de tao mask nguoi, sau do ghep voi mau nen, anh nen hoac video.",
        category = MediaPipeFeatureCategory.Vision,
        status = MediaPipeFeatureStatus.ReadyForEngine,
        inputType = "Camera frame / Image",
        outputType = "Segmentation mask",
        accentColor = Color(0xFF2E7D32),
    ),
    MediaPipeFeatureDefinition(
        id = "face_landmarks",
        title = "Face filter 3D",
        description = "Dung Face Landmarker, blendshapes va facial transform matrix de gan filter len mat.",
        category = MediaPipeFeatureCategory.Face,
        status = MediaPipeFeatureStatus.ReadyForEngine,
        inputType = "Camera frame",
        outputType = "Landmarks / Blendshapes / Matrix",
        accentColor = Color(0xFF1565C0),
    ),
    MediaPipeFeatureDefinition(
        id = "gesture_recognition",
        title = "Nhan dien cu chi tay",
        description = "Dung Gesture Recognizer de bat cu chi nhu open palm, thumbs up hoac pointing.",
        category = MediaPipeFeatureCategory.Body,
        status = MediaPipeFeatureStatus.ReadyForDesign,
        inputType = "Camera frame",
        outputType = "Gesture label",
        accentColor = Color(0xFF6A1B9A),
    ),
    MediaPipeFeatureDefinition(
        id = "pose_landmarks",
        title = "Pose tracking",
        description = "Dung Pose Landmarker de lay khung xuong co the cho fitness, dance hoac body effect.",
        category = MediaPipeFeatureCategory.Body,
        status = MediaPipeFeatureStatus.Planned,
        inputType = "Camera frame",
        outputType = "Pose landmarks",
        accentColor = Color(0xFFEF6C00),
    ),
    MediaPipeFeatureDefinition(
        id = "object_detection",
        title = "Object detection",
        description = "Phat hien vat the trong anh hoac camera va ve bounding box len preview.",
        category = MediaPipeFeatureCategory.Object,
        status = MediaPipeFeatureStatus.Planned,
        inputType = "Camera frame / Image",
        outputType = "Bounding boxes",
        accentColor = Color(0xFFC62828),
    ),
    MediaPipeFeatureDefinition(
        id = "image_classification",
        title = "Image classification",
        description = "Phan loai noi dung anh, phu hop lam tool demo nhanh cho MediaPipe Tasks.",
        category = MediaPipeFeatureCategory.Vision,
        status = MediaPipeFeatureStatus.Planned,
        inputType = "Image",
        outputType = "Category scores",
        accentColor = Color(0xFF00838F),
    ),
    MediaPipeFeatureDefinition(
        id = "effect_export",
        title = "Export ket qua",
        description = "Chuan bi flow xuat anh/video sau khi da ap dung segmentation, filter hoac sticker.",
        category = MediaPipeFeatureCategory.Utility,
        status = MediaPipeFeatureStatus.ReadyForDesign,
        inputType = "Edited preview",
        outputType = "Image / Video",
        accentColor = Color(0xFF455A64),
    ),
)
