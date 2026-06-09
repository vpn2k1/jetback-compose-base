package com.namvu.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.namvu.myapplication.mediapipe.MediaPipeTaskItem
import com.namvu.myapplication.ui.demo.ArtFilterArDemoScreen
import com.namvu.myapplication.ui.demo.AudioClassifierDemoScreen
import com.namvu.myapplication.ui.demo.FaceLandmarkerDemoScreen
import com.namvu.myapplication.ui.demo.GestureRecognizerDemoScreen
import com.namvu.myapplication.ui.demo.HandLandmarkerDemoScreen
import com.namvu.myapplication.ui.demo.ImageClassifierDemoScreen
import com.namvu.myapplication.ui.demo.ImageSegmenterDemoScreen
import com.namvu.myapplication.ui.demo.ObjectDetectorDemoScreen
import com.namvu.myapplication.ui.demo.PoseLandmarkerDemoScreen
import com.namvu.myapplication.ui.demo.TextClassifierDemoScreen
import com.namvu.myapplication.ui.home.MediaPipeTaskListScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = MediaPipeTaskRoute.Home,
    ) {
        composable(MediaPipeTaskRoute.Home) {
            MediaPipeTaskListScreen(
                onTaskClick = { task -> navController.navigateToTask(task) },
            )
        }
        composable(MediaPipeTaskRoute.FaceLandmarker) {
            FaceLandmarkerDemoScreen(onBack = navController::backToHome)
        }
        composable(MediaPipeTaskRoute.ImageSegmenter) {
            ImageSegmenterDemoScreen(onBack = navController::backToHome)
        }
        composable(MediaPipeTaskRoute.ObjectDetector) {
            ObjectDetectorDemoScreen(onBack = navController::backToHome)
        }
        composable(MediaPipeTaskRoute.PoseLandmarker) {
            PoseLandmarkerDemoScreen(onBack = navController::backToHome)
        }
        composable(MediaPipeTaskRoute.HandLandmarker) {
            HandLandmarkerDemoScreen(onBack = navController::backToHome)
        }
        composable(MediaPipeTaskRoute.GestureRecognizer) {
            GestureRecognizerDemoScreen(onBack = navController::backToHome)
        }
        composable(MediaPipeTaskRoute.ImageClassifier) {
            ImageClassifierDemoScreen(onBack = navController::backToHome)
        }
        composable(MediaPipeTaskRoute.TextClassifier) {
            TextClassifierDemoScreen(onBack = navController::backToHome)
        }
        composable(MediaPipeTaskRoute.AudioClassifier) {
            AudioClassifierDemoScreen(onBack = navController::backToHome)
        }
        composable(MediaPipeTaskRoute.ArtFilterAr) {
            ArtFilterArDemoScreen(onBack = navController::backToHome)
        }
    }
}

private fun NavHostController.navigateToTask(task: MediaPipeTaskItem) {
    navigate(task.route) {
        launchSingleTop = true
    }
}

private fun NavHostController.backToHome() {
    popBackStack(MediaPipeTaskRoute.Home, inclusive = false)
}
