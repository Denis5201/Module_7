package com.example.module7

import kotlin.Exception
import kotlin.math.pow

fun isOperation(s:String):Boolean {
    return when(s){
        "+","-","*","/","%","^" -> true
        else -> false}
}

fun isBoolOperation(s:String):Boolean {
    return when(s){
        "==","!=",">","<",">=","<=" -> true
        else -> false}
}

fun priority(s:String):Int {
    return when(s){
        "^" -> 1
        "*","/","%" -> 2
        "+","-" -> 3
        "==","!=",">","<",">=","<=" -> 4
        else -> 5
    }
}

fun getArray(inputStr:String, varArray:MutableList<Variable>, position:Int):MutableList<String> {
    val arrayRes = mutableListOf<String>()
    var i = 0
    while(i < inputStr.length) {
        if (inputStr[i] == ' ') {
            ++i
        }
        else if (inputStr[i] in 'a'..'z' || inputStr[i] in 'A'..'Z' || inputStr[i] in '0'..'9') {
            var newStr = ""
            while (i < inputStr.length &&
                (inputStr[i] in 'a'..'z' || inputStr[i] in 'A'..'Z' || inputStr[i] in '0'..'9' || inputStr[i] == '.' || inputStr[i] == '_')) {
                newStr += inputStr[i]
                ++i
            }
            if (newStr == "true") newStr="1"
            else if(newStr == "false") newStr="0"
            if (newStr.contains("[A-Za-z]".toRegex())) {
                if (newStr == "true") newStr="1"
                else if(newStr == "false") newStr="0"
                else {
                    val temp = varArray.find { it.name == newStr }
                    if (temp != null)
                        newStr = temp.commonValue.toString()
                    else
                        throw Exception("Получение значения, несуществующей переменной! Блок $position")
                }
            }

            arrayRes.add(newStr)
        }
        else if (isOperation(inputStr[i].toString()) || inputStr[i] == '(' || inputStr[i] == ')') {
            arrayRes.add(inputStr[i].toString())
            ++i
        }
        else if ((inputStr[i] == '=' || inputStr[i] == '!' || inputStr[i] == '<' || inputStr[i] == '>')
            && (i+1 < inputStr.length && (inputStr[i+1] == '='))) {
            arrayRes.add((inputStr[i].toString() + inputStr[i+1].toString()))
            i += 2
        }
        else if (inputStr[i] == '<' || inputStr[i] == '>') {
            arrayRes.add(inputStr[i].toString())
            ++i
        }
        else ++i
    }

    return arrayRes
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
            isOperation(i) ||  isBoolOperation(i) -> {
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
        "/" -> if (num2!=0.0) num1/num2 else throw Exception("Деление на 0!")
        "+" -> num1+num2
        "-" -> num1-num2
        "%" -> num1%num2
        "==" -> if (num1==num2) 1.0 else 0.0
        "!=" -> if (num1!=num2) 1.0 else 0.0
        ">" -> if (num1>num2) 1.0 else 0.0
        "<" -> if (num1<num2) 1.0 else 0.0
        ">=" -> if (num1>=num2) 1.0 else 0.0
        "<=" -> if (num1<=num2) 1.0 else 0.0
        else -> 0.0
    }
}

fun getResult(strRPN:MutableList<String>):String {
    val stack:MutableList<String> = mutableListOf()
    var result:Double
    for (i in strRPN) {
        if (isOperation(i) || isBoolOperation(i)) {
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

fun assignment(blockList:MutableList<Blocks>, variableArr: MutableList<Variable>, i:Int){
    var str = (blockList[i] as Blocks.Assignment).expression
    val name = (blockList[i] as Blocks.Assignment).name

    if (name.isEmpty()) throw Exception("Error! Введите имя переменной! Блок $i")
    str = if (str.isNotEmpty())
        getResult(toRPN(getArray(str, variableArr, i)))
    else
        "0"

    when ((blockList[i] as Blocks.Assignment).type) {
        "Int" -> variableArr.add(OurInteger(name, str.toDouble().toInt()))
        "Double" -> variableArr.add(OurDouble(name, str.toDouble()))
        "Bool" -> variableArr.add(OurBool(name, str.toDouble().toInt() != 0))
    }
}

fun changeVal(blockList:MutableList<Blocks>, variableArr: MutableList<Variable>, i:Int){
    var str = (blockList[i] as Blocks.ChangeVal).expression
    val name = (blockList[i] as Blocks.ChangeVal).name

    val temp = variableArr.find { it.name == name }
    if (temp != null) {
        if (str.isNotEmpty()) {
            str = getResult(toRPN(getArray(str, variableArr, i)))
            when (temp) {
                is OurInteger -> temp.value = str.toDouble().toInt()
                is OurDouble -> temp.commonValue = str.toDouble()
                is OurBool -> temp.value = str.toDouble().toInt() != 0
                else -> temp.commonValue = str.toDouble()
            }
        }
    }
    else throw Exception("Error! Изменение несуществующей переменной! Блок $i")
}

fun ifBlock(blockList:MutableList<Blocks>, variableArr: MutableList<Variable>, i:Int):Int{
    var str = (blockList[i] as Blocks.IfBlock).condition
    if (str.isNotEmpty())
        str = getResult(toRPN(getArray(str, variableArr, i)))
    else throw Exception("Error! Введите условие! Блок $i")

    var position = i
    if (str.toDouble().toInt() == 0) {
        while (position<blockList.size &&
            blockList[position] !is Blocks.EndIf &&
            blockList[position] !is Blocks.ElseBlock) {
            ++position
        }
    }
    return position-i
}

fun inLoop(blockList:MutableList<Blocks>, variableArr: MutableList<Variable>,
           i:Int, textEdit:String, resume:Int = 0, uslov:Boolean):Triple<Int, String, String> {
    val str = (blockList[i] as Blocks.WhileBlock).condition
    var value:String
    var change = ""
    var outString = ""
    var usl = uslov
    var count = 0
    if (str.isNotEmpty())
        value = getResult(toRPN(getArray(str, variableArr, i)))
    else throw Exception("Error! Введите условие! Блок $i")

    var position = i
    if (uslov && value.toDouble().toInt() == 0) {
        while (position<blockList.size && blockList[position] !is Blocks.EndWhile) {
            ++position
        }
    }
    else {
        position++
        if (!uslov) {
            while (position<i+resume && blockList[position] !is Blocks.WhileBlock && blockList[position] !is Blocks.EndWhile){
                ++position
                count++
            }
            if (blockList[position] is Blocks.EndWhile) {
                return Triple(position-i, outString, "")
            }
            else if (blockList[position] is Blocks.WhileBlock) {
                val res = inLoop(blockList, variableArr, position, textEdit, resume-(position-i), usl)
                if (res.third=="input") {
                    return Triple(position+res.first, outString+res.second ,"input")
                }
                if (res.third=="da") change="da"
                position += res.first+1
                count += res.first+1
                outString += res.second
            }
        }
        var name:String
        while (true) {
            count = if (usl) 0 else count
            if (!usl && change=="da") usl = true
            while (position < blockList.size && blockList[position] !is Blocks.EndWhile) {
                count++
                when (blockList[position]) {
                    is Blocks.Assignment -> {
                        assignment(blockList, variableArr, position)
                    }
                    is Blocks.ChangeVal -> {
                        changeVal(blockList, variableArr, position)
                    }
                    is Blocks.InputOutput -> {
                        name = (blockList[position] as Blocks.InputOutput).expression
                        if ((blockList[position] as Blocks.InputOutput).type == "Output") {
                            val temp = variableArr.find { it.name == name }
                            if (temp != null) {
                                outString += when (temp) {
                                    is OurInteger -> temp.value.toString() + "\n"
                                    is OurDouble -> temp.commonValue.toString() + "\n"
                                    is OurBool -> temp.value.toString() + "\n"
                                    else -> temp.commonValue.toString() + "\n"
                                }
                            } else throw Exception("Error! Вывод несуществующей переменной! Блок $i")
                        }
                        else {
                            if (usl) {
                                return Triple(count, outString ,"input")
                            }

                            usl = true
                            change="da"
                            val temp = variableArr.find { it.name == name }
                            if (temp != null) {
                                if (textEdit.isNotEmpty()){
                                    when (temp) {
                                        is OurInteger -> temp.value = textEdit.toDouble().toInt()
                                        is OurDouble -> temp.commonValue = textEdit.toDouble()
                                        is OurBool -> temp.value = textEdit.toDouble().toInt() != 0
                                        else -> temp.commonValue = textEdit.toDouble()
                                    }
                                }
                            }
                        }
                    }
                    is Blocks.IfBlock -> {
                        val temp = ifBlock(blockList, variableArr, position)
                        position += temp
                        count += temp
                    }
                    is Blocks.ElseBlock -> {
                        while (position < blockList.size && blockList[position] !is Blocks.EndIf) {
                            ++position
                        }
                    }
                    is Blocks.WhileBlock -> {
                        val res = inLoop(blockList, variableArr, position, textEdit, resume, usl)
                        if (res.third=="input") {
                            return Triple(position-i+res.first, res.second,"input")
                        }
                        position += res.first
                        count += res.first
                        outString += res.second
                    }
                }
                position++
            }
            value = getResult(toRPN(getArray(str, variableArr, i)))
            if (value.toDouble().toInt() == 0) {
                break
            }
            else position -= count
        }
    }
    return Triple(position-i, outString, change)
}