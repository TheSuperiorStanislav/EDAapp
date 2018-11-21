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

    fun createViewAddNet(context: Context, resources: Resources): Pair<View, Int> {
        val linearLayout = LinearLayout(context)
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        val scale = resources.displayMetrics.density
        val dpAsPixels16 = (16 * scale + 0.5f).toInt()
        val dpAsPixels8 = (8 * scale + 0.5f).toInt()
        lp.setMargins(dpAsPixels16, dpAsPixels8, dpAsPixels16, dpAsPixels8)
        val editText = generateEditText(context,lp)
        editText.hint = resources.getString(R.string.hint_add_net)
        linearLayout.addView(editText, lp)
        return Pair(linearLayout, editText.id)
    }

    fun createViewSaveFile(context: Context, resources: Resources): Pair<View, Pair<Int,Int>> {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        val scale = resources.displayMetrics.density
        val dpAsPixels16 = (16 * scale + 0.5f).toInt()
        val dpAsPixels8 = (8 * scale + 0.5f).toInt()
        lp.setMargins(dpAsPixels16, dpAsPixels8, dpAsPixels16, dpAsPixels8)
        val editText = generateEditText(context,lp)
        editText.hint = resources.getString(R.string.hint_save_file)
        val switch = generateSwitch(context,lp)
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                switch.text = ViewHelper.formatResStr(resources,
                        R.string.save_type, resources.getString(R.string.allegro))
            else
                switch.text = ViewHelper.formatResStr(resources,
                        R.string.save_type, resources.getString(R.string.calay90))
        }
        switch.text = ViewHelper.formatResStr(resources,
                R.string.save_type, resources.getString(R.string.calay90))
        linearLayout.addView(editText, lp)
        linearLayout.addView(switch, lp)
        return Pair(linearLayout, Pair(editText.id,switch.id))
    }

    fun createViewRoutingSettings(context: Context, resources: Resources): Pair<View, Array<Int>> {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        val scale = resources.displayMetrics.density
        val dpAsPixels16 = (16 * scale + 0.5f).toInt()
        val dpAsPixels8 = (8 * scale + 0.5f).toInt()
        lp.setMargins(dpAsPixels16, dpAsPixels8, dpAsPixels16, dpAsPixels8)
        val switchAlgorithm = generateSwitch(context,lp)
        switchAlgorithm.text = resources.getString(R.string.lee_algorithm)
        switchAlgorithm.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                switchAlgorithm.text = resources.getString(R.string.a_star_algorithm)
            else
                switchAlgorithm.text = resources.getString(R.string.lee_algorithm)
        }
        val switchDirection = generateSwitch(context,lp)
        switchDirection.text = resources.getString(R.string.orthogonal)
        switchDirection.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                switchDirection.text = resources.getString(R.string.orthogonal_diagonal)
            else
                switchDirection.text = resources.getString(R.string.orthogonal)
        }
        val switchIntersection = generateSwitch(context,lp)
        switchIntersection.text = resources.getString(R.string.intersection)
        linearLayout.addView(switchAlgorithm, lp)
        linearLayout.addView(switchDirection, lp)
        linearLayout.addView(switchIntersection, lp)
        return Pair(linearLayout, arrayOf(
                switchAlgorithm.id,
                switchDirection.id,
                switchIntersection.id))
    }

    private fun generateEditText(context: Context, lp:LinearLayout.LayoutParams): EditText {
        val editText = EditText(context)
        editText.id = View.generateViewId()
        editText.layoutParams = lp
        return editText
    }

    private fun generateSwitch(context: Context, lp:LinearLayout.LayoutParams): Switch {
        val switch = Switch(context)
        switch.id = View.generateViewId()
        switch.layoutParams = lp
        return switch
    }
}