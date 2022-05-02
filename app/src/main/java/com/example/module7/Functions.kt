package com.example.module7

import kotlin.math.pow

fun isOperation(s:String):Boolean {
    return when(s){
        "+","-","*","/","%","^" -> true
        else -> false}
}

fun priority(s:String):Int {
    return when(s){
        "^" -> 1
        "*","/","%" -> 2
        "+","-" -> 3
        else -> 4
    }
}

fun toRPN(str:MutableList<String>):MutableList<String> {
    val stack:MutableList<String> = mutableListOf()
    val outStr:MutableList<String> = mutableListOf()
    for (i in str) {
        when {
            i=="(" -> stack.add(i)
            i==")" -> {
                while (stack.last()!="(")
                    outStr.add(stack.removeAt(stack.size-1))
                stack.removeAt(stack.size-1)
            }
            isOperation(i) -> {
                if (stack.isNotEmpty()) {
                    if (priority(stack.last())>priority(i)) stack.add(i)
                    else {
                        while (stack.isNotEmpty() && priority(stack.last()) <= priority(i))
                            outStr.add(stack.removeAt(stack.size-1))
                        stack.add(i)
                    }
                }
                else stack.add(i)
            }
            else -> outStr.add(i)
        }
    }
    while (stack.isNotEmpty())
        outStr.add(stack.removeAt(stack.size-1))
    return outStr
}

fun calc(num1:Double, num2:Double, operation:String):Double {
    return when (operation) {
        "^" -> num1.pow(num2)
        "*" -> num1*num2
        "/" -> if (num2!=0.0) num1/num2 else 0.0
        "+" -> num1+num2
        "-" -> num1-num2
        "%" -> num1%num2
        else -> 0.0
    }
}

fun getResult(strRPN:MutableList<String>):String {
    val stack:MutableList<String> = mutableListOf()
    var result:Double
    for (i in strRPN) {
        if (isOperation(i)) {
            if (stack.size>1) {
                result = calc(stack[stack.size - 2].toDouble(), stack[stack.size - 1].toDouble(), i)
                stack.removeAt(stack.size-1)
                stack.removeAt(stack.size-1)
                stack.add(result.toString())
            }
        }
        else stack.add(i)
    }
    return stack.last()
}

fun getArray(inputStr:String, varArray:MutableList<Variable>):MutableList<String> {
    val arrayRes = mutableListOf<String>()
    var i = 0
    while(i < inputStr.length) {
        if (inputStr[i] == ' ') {
            ++i
        }
        else if (inputStr[i] in 'a'..'z' || inputStr[i] in 'A'..'Z' || inputStr[i] in '0'..'9') {
            var newStr = ""
            while (i < inputStr.length && (inputStr[i] in 'a'..'z' || inputStr[i] in 'A'..'Z' || inputStr[i] in '0'..'9' || inputStr[i] == '.')) {
                newStr += inputStr[i]
                ++i
            }
            if (newStr.contains("[A-Za-z]".toRegex())) {
                val temp = varArray.find { it.name == newStr }
                if (temp != null)
                    newStr = temp.commonValue.toString()
            }

            arrayRes.add((newStr))
        }
        else if (isOperation(inputStr[i].toString()) || inputStr[i] == '(' || inputStr[i] == ')') {
            arrayRes.add(inputStr[i].toString())
            ++i
        }
        else ++i
    }

    return arrayRes
}