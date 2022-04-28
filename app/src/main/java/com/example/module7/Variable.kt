package com.example.module7

open class Variable (val name:String, protected var isConst:Boolean = false, var value:Double = 0.0) {

}

class OurInteger(name:String, isConst:Boolean, value:Int = 0) : Variable(name, isConst, value.toDouble()) {

}

class OurDouble(name:String, isConst:Boolean, value:Double = 0.0) : Variable(name, isConst, value) {

}