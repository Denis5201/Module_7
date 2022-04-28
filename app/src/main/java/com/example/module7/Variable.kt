package com.example.module7

open class Variable (val name:String, val isConst:Boolean = false, var commonValue:Double = 0.0) {

}

class OurInteger(name:String, isConst:Boolean, commonValue: Double) : Variable(name, isConst, commonValue) {
    var value = commonValue.toInt()
    constructor(name:String, isConst:Boolean, value:Int = 0) : this(name, isConst, value.toDouble()){
        this.value = value
    }
}

class OurDouble(name:String, isConst:Boolean, value:Double = 0.0) : Variable(name, isConst, value) {

}