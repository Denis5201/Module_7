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

fun getNameOfArr(name:String):String {
    var nameOfArr = ""
    var index = 0
    while (index<name.length && name[index] != '[') {
        nameOfArr += name[index]
        ++index
    }
    return nameOfArr
}

fun getArray(inputStr:String, varArray:MutableList<Variable>,
             arrOfOurArr: MutableList<OurArr>, position:Int):MutableList<String> {
    val arrayRes = mutableListOf<String>()
    var i = 0
    while(i < inputStr.length) {
        if (inputStr[i] == ' ') {
            ++i
        }
        else if (inputStr[i] in 'a'..'z' || inputStr[i] in 'A'..'Z' || inputStr[i] in '0'..'9') {
            var newStr = ""
            while (i < inputStr.length &&
                (inputStr[i] in 'a'..'z' || inputStr[i] in 'A'..'Z' || inputStr[i] in '0'..'9'
                        || inputStr[i] == '.' || inputStr[i] == '_' || inputStr[i] == '[')) {
                newStr += inputStr[i]
                if (inputStr[i] == '[') {
                    ++i
                    while (i < inputStr.length && inputStr[i] != ']') {
                        newStr += inputStr[i]
                        ++i
                    }
                    newStr += inputStr[i]
                }
                ++i
            }
            if (newStr.contains("[A-Za-z]".toRegex())) {
                if (newStr == "true") newStr="1"
                else if(newStr == "false") newStr="0"
                else {
                    val temp = varArray.find { it.name == newStr }
                    var posStr = Regex("\\[.+\\]").find(newStr)?.value
                    if (temp != null)
                        newStr = temp.commonValue.toString()
                    else if (posStr!=null) {
                        val nameOfArr = getNameOfArr(newStr)
                        val temp2 = arrOfOurArr.find { it.name == nameOfArr }

                        if (temp2 != null) {
                            posStr = posStr.drop(1)
                            posStr = posStr.dropLast(1)
                            val pos = getResult(toRPN(getArray(posStr, varArray, arrOfOurArr, i))).toDouble().toInt()

                            if (pos >= 0 && pos < temp2.arrOfVar.size) {
                                newStr = temp2.arrOfVar[pos].commonValue.toString()
                            } else throw Exception("Индекс вне границ массива! Блок $i")
                        }
                        else throw Exception("Получение значения всего массива или элемента несуществующего массива! Блок $i")
                    }
                    else throw Exception("Получение значения несуществующей переменной! Блок $position")
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
        else throw Exception("Неизвестный символ! Блок $position")
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

fun createArr(expression:String, type:String):MutableList<Variable> {
    val newArr = mutableListOf<Variable>()
    var i = 0
    while(i < expression.length) {
        if (expression[i] == ' ' || expression[i] == ',') {
            ++i
        } else if (expression[i] in 'a'..'z' || expression[i] in '0'..'9') {
            var newStr = ""
            while (i < expression.length &&
                (expression[i] in 'a'..'z' || expression[i] in '0'..'9' || expression[i] == '.')
            ) {
                newStr += expression[i]
                ++i
            }
            if (newStr == "true") newStr = "1"
            else if (newStr == "false") newStr = "0"

            when (type) {
                "Int" -> newArr.add(OurInteger("i", newStr.toDouble().toInt()))
                "Double" -> newArr.add(OurDouble("d", newStr.toDouble()))
                "Bool" -> newArr.add(OurBool("b", newStr.toDouble().toInt() != 0))
            }
        } else throw Exception("Неизвестный символ при создании массива")
    }
    return newArr
}

fun assignment(blockList:MutableList<Blocks>, variableArr: MutableList<Variable>,
               arrOfOurArr: MutableList<OurArr>, i:Int) {
    var str = (blockList[i] as Blocks.Assignment).expression
    val name = (blockList[i] as Blocks.Assignment).name.filterNot { it == ' ' }
    val arrOfName = name.split(';').toMutableList()
    val arrOfExpr = str.split(';').toMutableList()

    if (name.isEmpty()) throw Exception("Введите имя переменной! Блок $i")

    while (arrOfName.isNotEmpty()) {
        if (arrOfName[0].contains(Regex("\\[.*\\]"))) {
            val nameOfArr = getNameOfArr(arrOfName[0])

            var size = Regex("\\[.+\\]").find(arrOfName[0])?.value
            if (size == null) {
                if (arrOfExpr.isNotEmpty()) {
                    arrOfOurArr.add(OurArr(nameOfArr, createArr(arrOfExpr[0],
                            (blockList[i] as Blocks.Assignment).type)))
                }
                else throw Exception("Невозможно создать массив и без указания размера, " +
                        "и без указания элементов! Блок $i")
            }
            else {
                val newArr = mutableListOf<Variable>()
                size = size.drop(1)
                size = size.dropLast(1)
                val len =
                    getResult(toRPN(getArray(size, variableArr, arrOfOurArr, i))).toDouble().toInt()
                for (c in 1..len) {
                    when ((blockList[i] as Blocks.Assignment).type) {
                        "Int" -> newArr.add(OurInteger("i", 0))
                        "Double" -> newArr.add(OurDouble("d", 0.0))
                        "Bool" -> newArr.add(OurBool("b", false))
                    }
                }
                arrOfOurArr.add(OurArr(nameOfArr, newArr))
            }
        } else {
            str = if (arrOfExpr.isNotEmpty() && arrOfExpr[0].isNotEmpty())
                getResult(toRPN(getArray(arrOfExpr[0], variableArr, arrOfOurArr, i)))
            else
                "0"

            when ((blockList[i] as Blocks.Assignment).type) {
                "Int" -> variableArr.add(OurInteger(arrOfName[0], str.toDouble().toInt()))
                "Double" -> variableArr.add(OurDouble(arrOfName[0], str.toDouble()))
                "Bool" -> variableArr.add(OurBool(arrOfName[0], str.toDouble().toInt() != 0))
            }
        }
        arrOfName.removeAt(0)
        if (arrOfExpr.isNotEmpty()) arrOfExpr.removeAt(0)
    }
}

fun changeVal(blockList:MutableList<Blocks>, variableArr: MutableList<Variable>,
              arrOfOurArr:MutableList<OurArr>, i:Int) {
    var str = (blockList[i] as Blocks.ChangeVal).expression
    val name = (blockList[i] as Blocks.ChangeVal).name.filterNot { it == ' ' }

    if (str.isNotEmpty()) {
        str = getResult(toRPN(getArray(str, variableArr, arrOfOurArr, i)))

        val temp = variableArr.find { it.name == name }
        if (temp != null) {
            when (temp) {
                is OurInteger -> temp.value = str.toDouble().toInt()
                is OurDouble -> temp.commonValue = str.toDouble()
                is OurBool -> temp.value = str.toDouble().toInt() != 0
                else -> temp.commonValue = str.toDouble()
            }
            return
        }

        val nameOfArr = getNameOfArr(name)
        val temp2 = arrOfOurArr.find { it.name == nameOfArr }
        if (temp2 != null) {
            var posStr = Regex("\\[.+\\]").find(name)?.value
            if (posStr!=null) {
                posStr = posStr.drop(1)
                posStr = posStr.dropLast(1)
                val pos = getResult(toRPN(getArray(posStr, variableArr, arrOfOurArr, i))).toDouble().toInt()

                if (pos>=0 && pos<temp2.arrOfVar.size) {
                    when (temp2.arrOfVar[pos]) {
                        is OurInteger -> (temp2.arrOfVar[pos] as OurInteger).value = str.toDouble().toInt()
                        is OurDouble -> temp2.arrOfVar[pos].commonValue = str.toDouble()
                        is OurBool -> (temp2.arrOfVar[pos] as OurBool).value = str.toDouble().toInt() != 0
                        else -> temp2.arrOfVar[pos].commonValue = str.toDouble()
                    }
                    return
                }
                else throw Exception("Индекс вне границ массива! Блок $i")
            }
            else throw Exception("Нельзя изменить массив в целом! Блок $i")
        }
        else throw Exception("Изменение несуществующей переменной! Блок $i")
    }
}

fun ifBlock(blockList:MutableList<Blocks>, variableArr: MutableList<Variable>,
            arrOfOurArr:MutableList<OurArr>, i:Int):Int{
    var str = (blockList[i] as Blocks.IfBlock).condition
    if (str.isNotEmpty())
        str = getResult(toRPN(getArray(str, variableArr, arrOfOurArr, i)))
    else throw Exception("Введите условие! Блок $i")

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

fun output(blockList:MutableList<Blocks>, variableArr: MutableList<Variable>,
           arrOfOurArr:MutableList<OurArr>, i:Int):String {
    val name = (blockList[i] as Blocks.InputOutput).expression.filterNot { it == ' ' }
    val arrOfName = name.split(',').toMutableList()

    var resultString = ""
    while (arrOfName.isNotEmpty()) {
        val temp = variableArr.find { it.name == arrOfName[0] }
        if (temp != null) {
            resultString += when (temp) {
                is OurInteger -> temp.value.toString() + "\n"
                is OurDouble -> temp.commonValue.toString() + "\n"
                is OurBool -> temp.value.toString() + "\n"
                else -> temp.commonValue.toString() + "\n"
            }
            arrOfName.removeAt(0)
            continue
        }

        var temp2 = arrOfOurArr.find { it.name == arrOfName[0] }
        if (temp2 != null) {
            for (el in temp2.arrOfVar) {
                resultString += when (el) {
                    is OurInteger -> el.value.toString() + " "
                    is OurDouble -> el.commonValue.toString() + " "
                    is OurBool -> el.value.toString() + " "
                    else -> el.commonValue.toString() + " "
                }
            }
            resultString += "\n"
            arrOfName.removeAt(0)
            continue
        }
        val nameOfArr = getNameOfArr(arrOfName[0])
        temp2 = arrOfOurArr.find { it.name == nameOfArr }
        if (temp2 != null) {
            var posStr = Regex("\\[.+\\]").find(name)?.value
            if (posStr != null) {
                posStr = posStr.drop(1)
                posStr = posStr.dropLast(1)
                val pos = getResult(toRPN(getArray(posStr, variableArr, arrOfOurArr, i))).toDouble()
                    .toInt()

                if (pos >= 0 && pos < temp2.arrOfVar.size) {
                    resultString += when (temp2.arrOfVar[0]) {
                        is OurInteger -> (temp2.arrOfVar[pos] as OurInteger).value.toString() + "\n"
                        is OurDouble -> temp2.arrOfVar[pos].commonValue.toString() + "\n"
                        is OurBool -> (temp2.arrOfVar[pos] as OurBool).value.toString() + "\n"
                        else -> temp2.arrOfVar[pos].commonValue.toString() + "\n"
                    }
                } else throw Exception("Индекс вне границ массива! Блок $i")
            } else throw Exception("Не указан индекс массива в []! Блок $i")
        } else throw Exception("Вывод несуществующей переменной! Блок $i")
        arrOfName.removeAt(0)
    }
    return resultString
}

fun endOfInput(blockList:MutableList<Blocks>, variableArr: MutableList<Variable>,
               arrOfOurArr:MutableList<OurArr>, i:Int, inputStr: String) {
    val name = (blockList[i] as Blocks.InputOutput).expression.filterNot { it == ' ' }
    val arrOfName = name.split(',').toMutableList()
    val inputArr = inputStr.split(' ').toMutableList()

    while (arrOfName.isNotEmpty() && inputArr.isNotEmpty()) {
        val temp = variableArr.find { it.name == arrOfName[0] }
        if (temp != null) {
            when (temp) {
                is OurInteger -> temp.value = inputArr[0].toDouble().toInt()
                is OurDouble -> temp.commonValue = inputArr[0].toDouble()
                is OurBool -> temp.value = inputArr[0].toDouble().toInt() != 0
                else -> temp.commonValue = inputArr[0].toDouble()
            }
            inputArr.removeAt(0)
            arrOfName.removeAt(0)
            continue
        }

        var temp2 = arrOfOurArr.find { it.name == arrOfName[0] }
        if (temp2 != null) {
            var pos = 0
            while (pos < temp2.arrOfVar.size && inputArr.isNotEmpty()) {
                when (temp2.arrOfVar[pos]) {
                    is OurInteger -> (temp2.arrOfVar[pos] as OurInteger).value = inputArr[0].toDouble().toInt()
                    is OurDouble -> temp2.arrOfVar[pos].commonValue = inputArr[0].toDouble()
                    is OurBool -> (temp2.arrOfVar[pos] as OurBool).value = inputArr[0].toDouble().toInt() != 0
                    else -> temp2.arrOfVar[pos].commonValue = inputArr[0].toDouble()
                }
                inputArr.removeAt(0)
                ++pos
            }
            arrOfName.removeAt(0)
            continue
        }
        val nameOfArr = getNameOfArr(arrOfName[0])
        temp2 = arrOfOurArr.find { it.name == nameOfArr }
        if (temp2 != null) {
            var posStr = Regex("\\[.+\\]").find(name)?.value
            if (posStr != null) {
                posStr = posStr.drop(1)
                posStr = posStr.dropLast(1)
                val pos = getResult(toRPN(getArray(posStr, variableArr, arrOfOurArr, i))).toDouble()
                    .toInt()

                if (pos >= 0 && pos < temp2.arrOfVar.size) {
                    when (temp2.arrOfVar[pos]) {
                        is OurInteger -> (temp2.arrOfVar[pos] as OurInteger).value = inputArr[0].toDouble().toInt()
                        is OurDouble -> temp2.arrOfVar[pos].commonValue = inputArr[0].toDouble()
                        is OurBool -> (temp2.arrOfVar[pos] as OurBool).value = inputArr[0].toDouble().toInt() != 0
                        else -> temp2.arrOfVar[pos].commonValue = inputArr[0].toDouble()
                    }
                } else throw Exception("Индекс вне границ массива! Блок $i")
            } else throw Exception("Не указан индекс массива в []! Блок $i")
        } else throw Exception("Ввод несуществующей переменной! Блок $i")
        inputArr.removeAt(0)
        arrOfName.removeAt(0)
    }
}

fun inLoop(blockList:MutableList<Blocks>, variableArr: MutableList<Variable>, arrOfOurArr:MutableList<OurArr>,
           i:Int, textEdit:String, resume:Int = 0, uslov:Boolean):Triple<Int, String, String> {
    val str = (blockList[i] as Blocks.WhileBlock).condition
    var value:String
    var change = ""
    var outString = ""
    var usl = uslov
    var count = 0
    if (str.isNotEmpty())
        value = getResult(toRPN(getArray(str, variableArr, arrOfOurArr, i)))
    else throw Exception("Введите условие! Блок $i")

    var position = i
    if (uslov && value.toDouble().toInt() == 0) {
        while (position<blockList.size && blockList[position] !is Blocks.EndWhile) {
            ++position
        }
    }
    else {
        position++
        if (!uslov) {
            while (position<i+resume) {
                while (position < i + resume && blockList[position] !is Blocks.WhileBlock && blockList[position] !is Blocks.EndWhile) {
                    ++position
                    count++
                }
                if (blockList[position] is Blocks.EndWhile) {
                    return Triple(position - i, outString, "")
                } else if (blockList[position] is Blocks.WhileBlock) {
                    val res = inLoop(blockList, variableArr, arrOfOurArr, position, textEdit,resume - (position - i), usl)
                    if (res.third == "input") {
                        return Triple(position - i + res.first, outString + res.second, "input")
                    }
                    if (res.third == "da") change = "da"
                    position += res.first + 1
                    count += res.first + 1
                    outString += res.second
                }
            }
        }
        while (true) {
            count = if (usl) 0 else count
            if (!usl && change=="da") usl = true
            while (position < blockList.size && blockList[position] !is Blocks.EndWhile) {
                count++
                when (blockList[position]) {
                    is Blocks.Assignment -> {
                        assignment(blockList, variableArr, arrOfOurArr, position)
                    }
                    is Blocks.ChangeVal -> {
                        changeVal(blockList, variableArr, arrOfOurArr, position)
                    }
                    is Blocks.InputOutput -> {
                        if ((blockList[position] as Blocks.InputOutput).type == "Output") {
                            outString += output(blockList, variableArr, arrOfOurArr, position)
                        }
                        else {
                            if (usl) {
                                return Triple(count, outString ,"input")
                            }

                            usl = true
                            change="da"
                            endOfInput(blockList, variableArr, arrOfOurArr, position, textEdit)
                        }
                    }
                    is Blocks.IfBlock -> {
                        val temp = ifBlock(blockList, variableArr, arrOfOurArr, position)
                        position += temp
                        count += temp
                    }
                    is Blocks.ElseBlock -> {
                        while (position < blockList.size && blockList[position] !is Blocks.EndIf) {
                            ++position
                            ++count
                        }
                    }
                    is Blocks.WhileBlock -> {
                        val res = inLoop(blockList, variableArr, arrOfOurArr, position, textEdit, resume, usl)
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
            value = getResult(toRPN(getArray(str, variableArr, arrOfOurArr, i)))
            if (value.toDouble().toInt() == 0) {
                break
            }
            else position -= count
        }
    }
    return Triple(position-i, outString, change)
}