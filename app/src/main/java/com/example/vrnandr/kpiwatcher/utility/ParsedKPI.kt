package com.example.vrnandr.kpiwatcher.utility

private const val VALUE_CHAR_COUNT = 5

data class ParsedKPI (val value: String, val color: String, val text: String)

fun convertKPI(raw:String): List<ParsedKPI> {
    val returnValue = mutableListOf<ParsedKPI>()
    for (s in raw.split(":")){
        var value = s.substringBefore(" ")
        val color = s.substringAfter(" ").substringBefore(" ")
        val text = s.substringAfter(" ").substringAfter(" ")
        if (value.length>VALUE_CHAR_COUNT) //если строка типа 98.55хххх то приводим к виду 98.55
            value = value.dropLast(value.length-VALUE_CHAR_COUNT)
        returnValue.add(ParsedKPI(value, color, text))
    }
    return  returnValue
}