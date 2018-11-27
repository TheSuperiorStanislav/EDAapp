package com.study.thesuperiorstanislav.edaapp.math

import com.study.thesuperiorstanislav.edaapp.editor.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawPoint
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.DrawType
import com.study.thesuperiorstanislav.edaapp.editor.domain.model.draw.ObjectType
import com.study.thesuperiorstanislav.edaapp.utils.math.AStarAlgorithm
import com.study.thesuperiorstanislav.edaapp.utils.math.LeeAlgorithm
import org.junit.Assert
import org.junit.Test

class AStarTests {
    @Test
    fun testAStarSimple() {
        val drawPoint = DrawPoint(0f,0f)
        val obj = DrawObject(drawPoint, ObjectType.Net, DrawType.NET)
        val emp: DrawObject? = null
        val drawMatrix = arrayOf(
                arrayOf(obj, obj, emp, emp, emp, obj),
                arrayOf(emp, obj, emp, obj, emp, obj),
                arrayOf(emp, obj, emp, emp, emp, obj),
                arrayOf(emp, emp, emp, emp, emp, obj))
        val aStarAlgorithm = AStarAlgorithm(drawMatrix)
        val startPoint = Point(3,0)
        val endPoint = Point(1,3)
        val algorithmReturnData = aStarAlgorithm.doTheThing(startPoint,endPoint,true)
        Assert.assertNotNull(algorithmReturnData)
    }

    @Test
    fun testAStarNoPath() {
        val drawPoint = DrawPoint(0f,0f)
        val obj = DrawObject(drawPoint, ObjectType.Net, DrawType.NET)
        val emp: DrawObject? = null
        val drawMatrix = arrayOf(
                arrayOf(obj, obj, emp, emp, emp, obj),
                arrayOf(emp, obj, emp, obj, emp, obj),
                arrayOf(emp, obj, emp, emp, emp, obj),
                arrayOf(emp, emp, obj, emp, emp, obj))
        val aStarAlgorithm = AStarAlgorithm(drawMatrix)
        val startPoint = Point(3,0)
        val endPoint = Point(1,3)
        val algorithmReturnData = aStarAlgorithm.doTheThing(startPoint,endPoint,true)
        Assert.assertNull(algorithmReturnData)
    }

    @Test
    fun testAStarFromLeeWiki() {
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
        val aStarAlgorithm = AStarAlgorithm(drawMatrix)
        val startPoint = Point(3, 6)
        val endPoint = Point(3, 0)
        var algorithmReturnData = aStarAlgorithm.doTheThing(startPoint, endPoint,true)
        Assert.assertNotNull(algorithmReturnData?.path)
        Assert.assertEquals(7,algorithmReturnData?.path?.size)
        algorithmReturnData = aStarAlgorithm.doTheThing(startPoint, endPoint,false)
        Assert.assertNotNull(algorithmReturnData?.path)
        Assert.assertEquals(11,algorithmReturnData?.path?.size)
    }

    @Test
    fun testAStarFromWiki() {
        val drawPoint = DrawPoint(0f, 0f)
        val obj = DrawObject(drawPoint, ObjectType.Net, DrawType.NET)
        val emp:DrawObject? = null
        val drawMatrix: Array<Array<DrawObject?>> = arrayOf(
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, obj, obj, obj, obj, emp, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, obj, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, obj, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, obj, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp),
                arrayOf(emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp, emp))
        val aStarAlgorithm = AStarAlgorithm(drawMatrix)
        val startPoint = Point(2, 7)
        val endPoint = Point(8, 1)
        var algorithmReturnData = aStarAlgorithm.doTheThing(startPoint, endPoint,true)
        Assert.assertNotNull(algorithmReturnData?.path)
        Assert.assertEquals(11,algorithmReturnData?.path?.size)
        algorithmReturnData = aStarAlgorithm.doTheThing(startPoint, endPoint,false)
        Assert.assertNotNull(algorithmReturnData?.path)
        Assert.assertEquals(13,algorithmReturnData?.path?.size)
    }
}