package com.study.thesuperiorstanislav.edaapp.main

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.study.thesuperiorstanislav.edaapp.R
import com.study.thesuperiorstanislav.edaapp.data.source.CircuitRepository
import com.study.thesuperiorstanislav.edaapp.main.domain.usecase.CacheDataFromFile
import com.study.thesuperiorstanislav.edaapp.main.domain.usecase.CreateMatrix
import com.study.thesuperiorstanislav.edaapp.main.domain.usecase.GetData
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import androidx.core.content.ContextCompat
import java.security.Permissions


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val ft = supportFragmentManager.beginTransaction()

        val fragment = MainFragment()
        fragment.setPresenter(MainPresenter(fragment, GetData(CircuitRepository), CacheDataFromFile(CircuitRepository)))
        ft.replace(R.id.content_frame, fragment)
        if (!isFinishing)
            ft.commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED) {
            super.onActivityResult(requestCode, resultCode, data)
            val fragment = supportFragmentManager.findFragmentById(R.id.content_frame)
            fragment?.onActivityResult(requestCode, resultCode, data)
        }
    }
}
