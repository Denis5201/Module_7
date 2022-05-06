package com.example.module7

open class Variable (val name:String, var commonValue:Double = 0.0) {}

class OurInteger(name:String, commonValue: Double) : Variable(name, commonValue) {
    var value = commonValue.toInt()
    set(value) {
        this.commonValue=value.toDouble()
        field=value
    }
    constructor(name:String, value:Int = 0) : this(name, value.toDouble()){
        this.value = value
    }
}

class OurDouble(name:String, value:Double = 0.0) : Variable(name, value) {}