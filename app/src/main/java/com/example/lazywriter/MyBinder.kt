package com.example.lazywriter

import android.os.Binder
import java.io.Serializable

class MyBinder(val servc:LocationService) : Binder() {
    fun getService():LocationService {
        return servc
    }
}
