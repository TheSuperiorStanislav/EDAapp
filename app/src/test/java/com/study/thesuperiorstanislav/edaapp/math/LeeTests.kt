package com.study.thesuperiorstanislav.edaapp.math


import com.study.thesuperiorstanislav.edaapp.main.domain.model.Point
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawObject
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawPoint
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.DrawType
import com.study.thesuperiorstanislav.edaapp.main.domain.model.draw.ObjectType
import com.study.thesuperiorstanislav.edaapp.utils.math.LeeAlgorithm
import org.junit.Assert
import org.junit.Test

class LeeTests {
    @Test
    fun testLeeSimple() {
        val drawPoint = DrawPoint(0f,0f)
        val obj = DrawObject(drawPoint,ObjectType.Net, DrawType.NET)
        val drawMatrix = arrayOf(
                arrayOf(obj, null, null, null, null, null),
                arrayOf(null, obj, null, obj, null, null),
                arrayOf(null, obj, null, obj, null, obj),
                arrayOf(null, null, null, null, null, obj))
        val leeAlgorithm = LeeAlgorithm(6,4,drawMatrix)
        val startPoint = Point(5,3)
        val endPoint = Point(0,0)
        val pathList = leeAlgorithm.doTheThing(startPoint,endPoint,true)
        Assert.assertNotNull(pathList)
    }

    @Test
    fun testLeeFromWiki() {
        val drawPoint = DrawPoint(0f, 0f)
        val obj = DrawObject(drawPoint, ObjectType.Net, DrawType.NET)
        val drawMatrix: Array<Array<DrawObject?>> = arrayOf(
                arrayOf<DrawObject?>(null, null, obj, null, null, null, null, null, null, null, null, null),
                arrayOf<DrawObject?>(null, null, obj, null, null, null, null, null, null, null, null, null),
                arrayOf<DrawObject?>(null, null, null, null, null, null, null, null, null, null, null, null),
                arrayOf<DrawObject?>(null, null, null, null, null, null, null, null, obj, obj, null, null),
                arrayOf<DrawObject?>(null, obj, obj, obj, obj, null, null, null, null, null, null, null),
                arrayOf<DrawObject?>(null, null, null, null, null, null, null, null, null, obj, obj, null),
                arrayOf<DrawObject?>(null, null, null, null, null, null, null, null, null, obj, obj, null),
                arrayOf<DrawObject?>(null, null, null, null, null, null, null, null, null, null, null, null))
        val leeAlgorithm = LeeAlgorithm(12, 8, drawMatrix)
        val startPoint = Point(3, 6)
        val endPoint = Point(3, 0)
        var pathList = leeAlgorithm.doTheThing(startPoint, endPoint,true)
        Assert.assertNotNull(pathList)
        Assert.assertEquals(7,pathList?.size)
        pathList = leeAlgorithm.doTheThing(startPoint, endPoint,false)
        Assert.assertNotNull(pathList)
        Assert.assertEquals(11,pathList?.size)
    }

    @Test
    fun testLeeNoPath() {
        val drawPoint = DrawPoint(0f,0f)
        val obj = DrawObject(drawPoint,ObjectType.Net, DrawType.NET)
        val drawMatrix = arrayOf(
                arrayOf(obj, obj, null, null, null, null),
                arrayOf(obj, obj, null, obj, null, null),
                arrayOf(null, obj, null, obj, null, obj),
                arrayOf(null, null, null, null, null, obj))
        val leeAlgorithm = LeeAlgorithm(6,4,drawMatrix)
        val startPoint = Point(5,3)
        val endPoint = Point(0,0)
        val pathList = leeAlgorithm.doTheThing(startPoint,endPoint,false)
        Assert.assertNull(pathList)
    }
}