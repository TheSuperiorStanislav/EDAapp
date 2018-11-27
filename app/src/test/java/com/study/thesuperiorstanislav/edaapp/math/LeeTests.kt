package com.study.thesuperiorstanislav.edaapp.math


import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawPoint
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawType
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.ObjectType
import com.study.thesuperiorstanislav.edaapp.utils.math.LeeAlgorithm
import org.junit.Assert
import org.junit.Test

class LeeTests {
    @Test
    fun testLeeSimple() {
        val drawPoint = DrawPoint(0f,0f)
        val obj = DrawObject(drawPoint,ObjectType.Net, DrawType.NET)
        val emp:DrawObject? = null
        val drawMatrix = arrayOf(
                arrayOf(obj, obj, emp, emp, emp, obj),
                arrayOf(emp, obj, emp, obj, emp, obj),
                arrayOf(emp, obj, emp, emp, emp, obj),
                arrayOf(emp, emp, emp, emp, emp, obj))
        val leeAlgorithm = LeeAlgorithm(drawMatrix)
        val startPoint = Point(3,0)
        val endPoint = Point(1,3)
        val leeReturnData = leeAlgorithm.doTheThing(startPoint,endPoint,true)
        Assert.assertNotNull(leeReturnData.path)
    }

    @Test
    fun testLeeFromWiki() {
        val drawPoint = DrawPoint(0f, 0f)
        val obj = DrawObject(drawPoint, ObjectType.Net, DrawType.NET)
        val emp:DrawObject? = null
        val drawMatrix: Array<Array<DrawObject?>> = arrayOf(
                arrayOf(emp, emp, obj, emp, emp, emp, emp, emp, emp, emp, emp, emp),
                arrayOf(emp, emp, obj, emp, emp, emp, emp, emp, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, obj, obj, emp, emp),
                arrayOf(emp, obj, obj, obj, obj, emp, emp, emp, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, emp, obj, obj, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, emp, obj, obj, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp))
        val leeAlgorithm = LeeAlgorithm(drawMatrix)
        val startPoint = Point(3, 6)
        val endPoint = Point(3, 0)
        var leeReturnData = leeAlgorithm.doTheThing(startPoint, endPoint,true)
        Assert.assertNotNull(leeReturnData.path)
        Assert.assertEquals(7,leeReturnData.path?.size)
        leeReturnData = leeAlgorithm.doTheThing(startPoint, endPoint,false)
        Assert.assertNotNull(leeReturnData.path)
        Assert.assertEquals(11,leeReturnData.path?.size)
    }

    @Test
    fun testLeeIntersection() {
        val drawPoint = DrawPoint(0f, 0f)
        val obj = DrawObject(drawPoint, ObjectType.Net, DrawType.NET)
        val emp:DrawObject? = null
        val drawMatrix: Array<Array<DrawObject?>> = arrayOf(
                arrayOf(emp, obj, obj, emp, emp, emp, emp, emp, emp, emp, emp, emp),
                arrayOf(obj, emp, obj, emp, emp, emp, emp, emp, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, obj, obj, emp, emp),
                arrayOf(emp, obj, obj, obj, obj, emp, emp, emp, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, emp, obj, obj, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, emp, obj, obj, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp))
        val leeAlgorithm = LeeAlgorithm(drawMatrix)
        val startPoint = Point(6, 6)
        val endPoint = Point(0, 0)
        val leeReturnData = leeAlgorithm.doTheThing(startPoint, endPoint,true)
        Assert.assertNull(leeReturnData.path)
    }

    @Test
    fun testLeeNoPath() {
        val drawPoint = DrawPoint(0f,0f)
        val obj = DrawObject(drawPoint,ObjectType.Net, DrawType.NET)
        val emp:DrawObject? = null
        val drawMatrix = arrayOf(
                arrayOf(obj, obj, emp, emp, emp, emp),
                arrayOf(obj, obj, emp, obj, emp, emp),
                arrayOf(emp, obj, emp, obj, emp, obj),
                arrayOf(emp, emp, emp, emp, emp, obj))
        val leeAlgorithm = LeeAlgorithm(drawMatrix)
        val startPoint = Point(5,3)
        val endPoint = Point(0,0)
        val leeReturnData = leeAlgorithm.doTheThing(startPoint,endPoint,false)
        Assert.assertNull(leeReturnData)
    }
}