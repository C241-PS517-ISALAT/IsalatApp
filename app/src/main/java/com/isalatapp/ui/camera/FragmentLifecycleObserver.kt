package com.isalatapp.ui.camera

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class FragmentLifecycleObserver(private val onFragmentDestroy: () -> Unit) : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        onFragmentDestroy()
    }
}
