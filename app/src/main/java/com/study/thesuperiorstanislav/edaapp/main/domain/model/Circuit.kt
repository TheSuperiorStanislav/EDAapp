package com.study.thesuperiorstanislav.edaapp.main.domain.model

class Circuit(val listElements: MutableList<Element>,
              val listNets: MutableList<Net>,
              val listPins: MutableList<Pin>){
        fun generateElementName(type : String): String {
            var count = 1
            listElements.forEach {
                if (it.typeElement == type)
                    count++
            }
            return "$type$count"
        }
}