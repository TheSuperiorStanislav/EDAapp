package com.study.thesuperiorstanislav.edaapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.study.thesuperiorstanislav.edaapp.data.source.CircuitRepository
import com.study.thesuperiorstanislav.edaapp.editor.EditorFragment
import com.study.thesuperiorstanislav.edaapp.editor.EditorPresenter
import com.study.thesuperiorstanislav.edaapp.editor.domain.usecase.CacheDataFromFile
import com.study.thesuperiorstanislav.edaapp.editor.domain.usecase.CacheDrawMatrix
import com.study.thesuperiorstanislav.edaapp.editor.domain.usecase.GetData
import kotlinx.android.synthetic.main.activity_nav.*
import kotlinx.android.synthetic.main.app_bar_nav.*

class NavActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        showFragment(nav_view.checkedItem?.itemId)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        if (item.isChecked)
            return true

        showFragment(item.itemId)

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED) {
            super.onActivityResult(requestCode, resultCode, data)
            val fragment = supportFragmentManager.findFragmentById(R.id.content_frame)
            fragment?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showFragment(id: Int?) {
        if (id == null) {
            val ft = supportFragmentManager.beginTransaction()
            val fragment = EditorFragment()
            fragment.setPresenter(EditorPresenter(fragment, GetData(CircuitRepository), CacheDataFromFile(CircuitRepository), CacheDrawMatrix(CircuitRepository)))
            ft.replace(R.id.content_frame, fragment)
            if (!isFinishing)
                ft.commitAllowingStateLoss()
        }
        when (id) {
            R.id.nav_editor -> {
                val ft = supportFragmentManager.beginTransaction()
                val fragment = EditorFragment()
                fragment.setPresenter(EditorPresenter(fragment, GetData(CircuitRepository), CacheDataFromFile(CircuitRepository), CacheDrawMatrix(CircuitRepository)))
                ft.replace(R.id.content_frame, fragment)
                if (!isFinishing)
                    ft.commitAllowingStateLoss()
            }
            R.id.nav_routing -> {
            }
        }
    }
}
