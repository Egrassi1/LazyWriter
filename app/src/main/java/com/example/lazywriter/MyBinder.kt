package com.example.lazywriter

import android.os.Binder

class MyBinder(val servc:LocationService) : Binder() {
    fun getService():LocationService {
        return servc
    }
}
