package com.study.thesuperiorstanislav.edaapp.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.study.thesuperiorstanislav.edaapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val ft = supportFragmentManager.beginTransaction()

        val fragment = MainFragment()
        fragment.setPresenter(MainPresenter(fragment))
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
