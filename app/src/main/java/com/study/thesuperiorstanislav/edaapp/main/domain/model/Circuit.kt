package com.study.thesuperiorstanislav.edaapp.main.domain.model

class Circuit(val listElements: MutableList<Element>,
              val listNets: MutableList<Net>,
              val listPins: MutableList<Pin>)