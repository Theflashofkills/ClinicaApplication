package com.thiagojunhonma.devhealthy.Database

import android.app.Application
import com.couchbase.lite.CouchbaseLite

class DevHealthyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CouchbaseLite.init(this) // Inicializa o Couchbase Lite
    }
}
