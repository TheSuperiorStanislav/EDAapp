package com.study.thesuperiorstanislav.edaapp.math

import com.study.thesuperiorstanislav.edaapp.utils.math.MatrixUtils
import org.junit.Assert
import org.junit.Test

class MatrixTests {

    @Test
    fun testTranspose1() {
        val matrix = arrayOf(arrayOf(4,2), arrayOf(9,0))
        val expectedMatrix = arrayOf(arrayOf(4,9), arrayOf(2,0))
        MatrixUtils.transpose(matrix).forEachIndexed { index, ints ->
                Assert.assertArrayEquals(expectedMatrix[index],ints)
        }
    }

    @Test
    fun testTranspose2() {
        val matrix = arrayOf(arrayOf(2, 1), arrayOf(-3, 0), arrayOf(4, -1))
        val expectedMatrix = arrayOf(arrayOf(2, -3, 4), arrayOf(1, 0, -1))
        MatrixUtils.transpose(matrix).forEachIndexed { index, ints ->
            Assert.assertArrayEquals(expectedMatrix[index], ints)
        }
    }

    @Test
    fun testTranspose3() {
        val matrix = arrayOf(arrayOf(2, -3, 4), arrayOf(1, 0, -1))
        val expectedMatrix = arrayOf(arrayOf(2, 1), arrayOf(-3, 0), arrayOf(4, -1))
        MatrixUtils.transpose(matrix).forEachIndexed { index, ints ->
            Assert.assertArrayEquals(expectedMatrix[index], ints)
        }
    }

    @Test
    fun testMultiply1() {
        val matrixA = arrayOf(arrayOf(1,-1), arrayOf(2, 0), arrayOf(3,-0))
        val matrixB = arrayOf(arrayOf(1, 1), arrayOf(2, 0))
        val expectedMatrix = arrayOf(arrayOf(-1, 1), arrayOf(2, 2), arrayOf(3, 3))
        MatrixUtils.multiply(matrixA,matrixB).forEachIndexed { index, ints ->
            Assert.assertArrayEquals(expectedMatrix[index],ints)
        }
    }

    @Test
    fun testMultiply2() {
        val matrixA = arrayOf(arrayOf(-8,-10,-8), arrayOf(-1, 8, 0), arrayOf(-8,-4, 7))
        val matrixB = arrayOf(arrayOf(0, -7, -10), arrayOf(-3, 4, -10), arrayOf(-1, 6, -9))
        val expectedMatrix = arrayOf(arrayOf(38, -32, 252), arrayOf(-24, 39, -70), arrayOf(5, 82, 57))
        MatrixUtils.multiply(matrixA,matrixB).forEachIndexed { index, ints ->
            Assert.assertArrayEquals(expectedMatrix[index],ints)
        }
    }

    @Test
    fun testCreateR1() {
        val matrixQ = arrayOf(
                arrayOf(1, 1, 1, 0, 0, 0),
                arrayOf(0, 0, 1, 1, 1, 0),
                arrayOf(0, 1, 1, 1, 0, 0),
                arrayOf(0, 0, 0, 1, 0, 1))
        val matrixQt = MatrixUtils.transpose(matrixQ)
        matrixQt.toString()
        val expectedMatrix = arrayOf(
                arrayOf(0, 1, 2, 0),
                arrayOf(1, 0, 2, 1),
                arrayOf(2, 2, 0, 1),
                arrayOf(0, 1, 1, 0))
        MatrixUtils.createMatrixR(matrixQ).forEachIndexed { index, ints ->
            Assert.assertArrayEquals(expectedMatrix[index],ints)
        }
    }


}