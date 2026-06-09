import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()
    
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    androidLibrary {
       namespace = "com.namvu.myapplication.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.mediapipe.tasks.vision)
            implementation(libs.mediapipe.tasks.text)
            implementation(libs.mediapipe.tasks.audio)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.navigation.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
tasks.register("downloadMediaPipeModels") {
    val assetDir = file("$projectDir/src/androidMain/assets")
    val deeplabFile = file("$assetDir/deeplabv3.tflite")
    val hairFile = file("$assetDir/hair_segmenter.tflite")
    val selfieFile = file("$assetDir/selfie_segmenter.tflite")
    val selfieMulticlassFile = file("$assetDir/selfie_multiclass.tflite")
    val faceLandmarkerFile = file("$assetDir/face_landmarker.task")
    val objectDetectorFile = file("$assetDir/efficientdet_lite0.tflite")
    val imageClassifierFile = file("$assetDir/efficientnet_lite0.tflite")
    val poseLandmarkerFile = file("$assetDir/pose_landmarker_lite.task")
    val handLandmarkerFile = file("$assetDir/hand_landmarker.task")
    val gestureRecognizerFile = file("$assetDir/gesture_recognizer.task")
    val textClassifierFile = file("$assetDir/bert_classifier.tflite")
    val audioClassifierFile = file("$assetDir/yamnet.tflite")

    doLast {
        if (!assetDir.exists()) assetDir.mkdirs()

        // Tải DeepLab V3 nếu chưa có
        if (!deeplabFile.exists()) {
            println("📥 Đang tải Model DeepLabV3...")
            ant.invokeMethod("get", mapOf(
                "src" to "https://storage.googleapis.com/mediapipe-models/image_segmenter/deeplab_v3/float32/1/deeplab_v3.tflite",
                "dest" to deeplabFile
            ))
        }

        // Tải Hair Segmenter nếu chưa có
        if (!hairFile.exists()) {
            println("📥 Đang tải Model Hair Segmenter...")
            ant.invokeMethod("get", mapOf(
                "src" to "https://storage.googleapis.com/mediapipe-models/image_segmenter/hair_segmenter/float32/1/hair_segmenter.tflite",
                "dest" to hairFile
            ))
        }
        if (!selfieFile.exists()) {
            println("📥 Đang tải Model Selfie Segmenter...")
            ant.invokeMethod("get", mapOf(
                "src" to "https://storage.googleapis.com/mediapipe-models/image_segmenter/selfie_segmenter/float16/1/selfie_segmenter.tflite",
                "dest" to selfieFile
            ))
        }
        if (!selfieMulticlassFile.exists()) {
            println("📥 Đang tải Model Selfie Segmenter...")
            ant.invokeMethod("get", mapOf(
                "src" to "https://storage.googleapis.com/mediapipe-models/image_segmenter/selfie_multiclass_256x256/float32/1/selfie_multiclass_256x256.tflite",
                "dest" to selfieMulticlassFile
            ))
        }
        if (!faceLandmarkerFile.exists()) {
            println("📥 Đang tải Model Face Landmarker...")
            ant.invokeMethod("get", mapOf(
                "src" to "https://storage.googleapis.com/mediapipe-models/face_landmarker/face_landmarker/float16/1/face_landmarker.task",
                "dest" to faceLandmarkerFile
            ))
        }
        if (!objectDetectorFile.exists()) {
            println("📥 Đang tải Model Object Detector...")
            ant.invokeMethod("get", mapOf(
                "src" to "https://storage.googleapis.com/mediapipe-models/object_detector/efficientdet_lite0/int8/1/efficientdet_lite0.tflite",
                "dest" to objectDetectorFile
            ))
        }
        if (!imageClassifierFile.exists()) {
            println("📥 Đang tải Model Image Classifier...")
            ant.invokeMethod("get", mapOf(
                "src" to "https://storage.googleapis.com/mediapipe-models/image_classifier/efficientnet_lite0/float32/1/efficientnet_lite0.tflite",
                "dest" to imageClassifierFile
            ))
        }
        if (!poseLandmarkerFile.exists()) {
            println("📥 Đang tải Model Pose Landmarker...")
            ant.invokeMethod("get", mapOf(
                "src" to "https://storage.googleapis.com/mediapipe-models/pose_landmarker/pose_landmarker_lite/float16/1/pose_landmarker_lite.task",
                "dest" to poseLandmarkerFile
            ))
        }
        if (!handLandmarkerFile.exists()) {
            println("📥 Đang tải Model Hand Landmarker...")
            ant.invokeMethod("get", mapOf(
                "src" to "https://storage.googleapis.com/mediapipe-models/hand_landmarker/hand_landmarker/float16/1/hand_landmarker.task",
                "dest" to handLandmarkerFile
            ))
        }
        if (!gestureRecognizerFile.exists()) {
            println("📥 Đang tải Model Gesture Recognizer...")
            ant.invokeMethod("get", mapOf(
                "src" to "https://storage.googleapis.com/mediapipe-models/gesture_recognizer/gesture_recognizer/float16/1/gesture_recognizer.task",
                "dest" to gestureRecognizerFile
            ))
        }
        if (!textClassifierFile.exists()) {
            println("📥 Đang tải Model Text Classifier...")
            ant.invokeMethod("get", mapOf(
                "src" to "https://storage.googleapis.com/mediapipe-models/text_classifier/bert_classifier/float32/1/bert_classifier.tflite",
                "dest" to textClassifierFile
            ))
        }
        if (!audioClassifierFile.exists()) {
            println("📥 Đang tải Model Audio Classifier...")
            ant.invokeMethod("get", mapOf(
                "src" to "https://storage.googleapis.com/mediapipe-models/audio_classifier/yamnet/float32/1/yamnet.tflite",
                "dest" to audioClassifierFile
            ))
        }
        println("✅ Hoàn thành kiểm tra dữ liệu Models.")
    }
}

// Ép hệ thống chạy kiểm tra/tải trước khi compile Android
tasks.matching { it.name in setOf("androidPreBuild", "preAndroidMainBuild") }.configureEach {
    dependsOn("downloadMediaPipeModels")
}
