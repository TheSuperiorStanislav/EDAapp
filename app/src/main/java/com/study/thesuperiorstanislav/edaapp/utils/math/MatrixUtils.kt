package com.study.thesuperiorstanislav.edaapp.utils.math

object MatrixUtils{

    fun createMatrixR(matrixQ: Array<Array<Int>>):Array<Array<Int>>{
        val matrixR = multiply(matrixQ, transpose(matrixQ))
        matrixR.forEachIndexed { indexColumn, ints ->
            ints.forEachIndexed { indexRow, i ->
                if (indexColumn==indexRow)
                    ints[indexRow] = 0
            }
        }
        return matrixR
    }

    fun createMatrixQ(matrixA: Array<Array<Int>>,matrixB: Array<Array<Int>>): Array<Array<Int>>{
        return multiply(matrixB, transpose(matrixA))
    }

    @Throws(IndexOutOfBoundsException::class)
    fun transpose(matrix: Array<Array<Int>>):Array<Array<Int>>{
        return Array(matrix.first().size) { indexRow ->
            Array(matrix.size) { indexColumn ->
                matrix[indexColumn][indexRow]
            }
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun multiply(matrixA: Array<Array<Int>>,matrixB: Array<Array<Int>>):Array<Array<Int>>{
        return Array(matrixA.size) { indexColumn ->
            Array(matrixB.first().size) { indexRow ->
                var c = 0
                for (index in 0 until matrixB.size){
                    c += matrixA[indexColumn][index]*matrixB[index][indexRow]
                }
                c
            }
        }
    }

}