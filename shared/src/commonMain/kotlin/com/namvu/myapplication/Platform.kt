package com.namvu.myapplication

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform