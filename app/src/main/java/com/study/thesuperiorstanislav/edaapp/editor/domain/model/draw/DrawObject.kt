package com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw

data class DrawObject(var drawPoint: DrawPoint,var objectType: ObjectType,var drawType: DrawType){
    fun copy(): DrawObject {
        return DrawObject(drawPoint,objectType,drawType)
    }
}