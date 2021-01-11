package com.reactivemobile.websocketdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commitNow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = DrawingFragment.newInstance()
        supportFragmentManager.commitNow {
            replace(
                R.id.rootView,
                fragment,
                DrawingFragment.TAG
            )
        }
    }
}