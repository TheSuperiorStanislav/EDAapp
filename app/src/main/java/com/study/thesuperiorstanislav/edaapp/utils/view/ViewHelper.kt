package com.study.thesuperiorstanislav.edaapp.utils.view

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
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

    fun showSnackBar(view:View, message: String) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackBar.setAction("¯\\(°_o)/¯") { }
        snackBar.show()
    }

    fun showSnackBar(view:View, idStr: Int) {
        val snackBar = Snackbar.make(view, idStr, Snackbar.LENGTH_SHORT)
        snackBar.setAction("¯\\(°_o)/¯") { }
        snackBar.show()
    }

    fun showToast(context:Context, message: String) {
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
        val editText = EditText(context)
        editText.id = View.generateViewId()
        editText.layoutParams = lp
        editText.hint = resources.getString(R.string.hint_add_net)
        linearLayout.addView(editText, lp)
        return Pair(linearLayout, editText.id)
    }

    fun createViewWithEditTextAndSwitch(context: Context, resources: Resources): Pair<View, Pair<Int,Int>> {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        val scale = resources.displayMetrics.density
        val dpAsPixels16 = (16 * scale + 0.5f).toInt()
        val dpAsPixels8 = (8 * scale + 0.5f).toInt()
        lp.setMargins(dpAsPixels16, dpAsPixels8, dpAsPixels16, dpAsPixels8)
        val editText = EditText(context)
        editText.id = View.generateViewId()
        editText.layoutParams = lp
        editText.hint = resources.getString(R.string.hint_save_file)
        val switch = Switch(context)
        switch.id = View.generateViewId()
        switch.layoutParams = lp
        linearLayout.addView(editText, lp)
        linearLayout.addView(switch, lp)
        return Pair(linearLayout, Pair(editText.id,switch.id))
    }
}