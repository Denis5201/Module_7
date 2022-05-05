package com.example.module7

sealed class Blocks {
    data class Assignment(var name:String = "", var expression:String = ""):Blocks()
    data class ChangeVal(var expression:String = ""):Blocks()
}