package com.example.vrnandr.kpiwatcher.utility

data class ParsedKPI (val value: String, val color: String, val text: String)

fun convertKPI(raw:String): List<ParsedKPI> {
    val returnValue = mutableListOf<ParsedKPI>()
    for (s in raw.split(":")){
        var value = s.substringBefore(" ")
        val color = s.substringAfter(" ").substringBefore(" ")
        val text = s.substringAfter(" ").substringAfter(" ")
        if (value.length>5) //если строка типа 98.55хххх то приводим к виду 98.55
            value = value.dropLast(value.length-5)
        returnValue.add(ParsedKPI(value, color, text))
    }
    return  returnValue
}