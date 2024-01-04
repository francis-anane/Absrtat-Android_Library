package com.abstratsystems.abstratlibrary

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A utility object for running tasks in the background using coroutines and Dispatchers.IO.
 */
object AbstratBackgroundRunner {

    /**
     * Runs the provided code block in the background using a coroutine scope and Dispatchers.IO.
     *
     * @param codeBlock the code block to be executed in the background.
     *  @param updateMain a code block to update objects on the main thread with data from the background thread.
     */
    fun runInBackground(codeBlock: suspend () -> Unit, updateMain: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            codeBlock() // Runs in background

            // Update Main thread with data from background thread
            withContext(Dispatchers.Main){
                updateMain.invoke() // update main thread with data

            }
        }
    }
}
