package com.example.vrnandr.kpiwatcher.logger

import timber.log.Timber

const val TAG = "my"

class MyDebugTree: Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, "$TAG: $message", t)
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        return super.createStackElementTag(element) + " - " + element.lineNumber
    }
}