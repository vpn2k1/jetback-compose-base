package com.namvu.myapplication.mediapipe

import com.namvu.myapplication.navigation.MediaPipeTaskRoute

val defaultMediaPipeTasks = listOf(
    MediaPipeTaskItem(
        id = "face_landmarker",
        title = "Face Landmarker",
        description = "Face filter, face landmarks, blendshapes, facial transformation matrix.",
        route = MediaPipeTaskRoute.FaceLandmarker,
    ),
    MediaPipeTaskItem(
        id = "image_segmenter",
        title = "Image Segmenter",
        description = "Tach nguoi khoi nen, thay nen bang mau hoac anh.",
        route = MediaPipeTaskRoute.ImageSegmenter,
    ),
    MediaPipeTaskItem(
        id = "object_detector",
        title = "Object Detector",
        description = "Phat hien vat the va ve bounding boxes.",
        route = MediaPipeTaskRoute.ObjectDetector,
    ),
    MediaPipeTaskItem(
        id = "pose_landmarker",
        title = "Pose Landmarker",
        description = "Nhan dien dang nguoi va ve skeleton.",
        route = MediaPipeTaskRoute.PoseLandmarker,
    ),
    MediaPipeTaskItem(
        id = "hand_landmarker",
        title = "Hand Landmarker",
        description = "Nhan dien ban tay va ve hand landmarks.",
        route = MediaPipeTaskRoute.HandLandmarker,
    ),
    MediaPipeTaskItem(
        id = "gesture_recognizer",
        title = "Gesture Recognizer",
        description = "Nhan dien cu chi tay.",
        route = MediaPipeTaskRoute.GestureRecognizer,
    ),
    MediaPipeTaskItem(
        id = "image_classifier",
        title = "Image Classifier",
        description = "Phan loai anh.",
        route = MediaPipeTaskRoute.ImageClassifier,
    ),
    MediaPipeTaskItem(
        id = "text_classifier",
        title = "Text Classifier",
        description = "Phan loai van ban.",
        route = MediaPipeTaskRoute.TextClassifier,
    ),
    MediaPipeTaskItem(
        id = "audio_classifier",
        title = "Audio Classifier",
        description = "Phan loai am thanh.",
        route = MediaPipeTaskRoute.AudioClassifier,
    ),
    MediaPipeTaskItem(
        id = "art_filter_ar",
        title = "Art Filter AR",
        description = "Artwork AR filters using face landmarks, blendshapes and matrix.",
        route = MediaPipeTaskRoute.ArtFilterAr,
    ),
)
