package com.example.module7

sealed class Blocks {
    data class Assignment(var name:String = "", var expression:String = ""):Blocks()
    data class ChangeVal(var name:String = "", var expression:String = ""):Blocks()
    data class InputOutput(var expression:String = ""):Blocks()
}