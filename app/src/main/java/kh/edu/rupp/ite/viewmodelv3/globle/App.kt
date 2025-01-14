package kh.edu.rupp.ite.viewmodelv3.globle

import android.app.Application

class App : Application() {


    override fun onCreate() {
        super.onCreate()

        instance = this
    }

    companion object {
        private lateinit var instance : App;

        fun get(): App = instance
    }

}