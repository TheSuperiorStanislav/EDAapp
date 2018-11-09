package com.study.thesuperiorstanislav.edaapp.utils.view

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.study.thesuperiorstanislav.edaapp.R

object ViewHelper {
    fun formatResStr(resources: Resources,idStr: Int, obj: Any): String {
        return String.format(resources.getString(idStr), obj)
    }

    fun formatResStr(resources: Resources,idStr: Int, obj1: Any, obj2: Any): String {
        return String.format(resources.getString(idStr), obj1, obj2)
    }

    fun onError(view:View,message: String) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackBar.setAction("¯\\(°_o)/¯") { }
        snackBar.show()
    }

    fun onError(view:View,idStr: Int) {
        val snackBar = Snackbar.make(view, idStr, Snackbar.LENGTH_SHORT)
        snackBar.setAction("¯\\(°_o)/¯") { }
        snackBar.show()
    }

    fun onErrorToast(context:Context,message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun createViewWithEditText(context: Context, resources: Resources): Pair<View, Int> {
        val linearLayout = LinearLayout(context)
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        val scale = resources.displayMetrics.density
        val dpAsPixels16 = (16 * scale + 0.5f).toInt()
        val dpAsPixels8 = (8 * scale + 0.5f).toInt()
        lp.setMargins(dpAsPixels16, dpAsPixels8, dpAsPixels16, dpAsPixels8)
        val input = EditText(context)
        input.id = View.generateViewId()
        input.layoutParams = lp
        input.hint = resources.getString(R.string.hint_add_net)
        linearLayout.addView(input, lp)
        return Pair(linearLayout, input.id)
    }
}