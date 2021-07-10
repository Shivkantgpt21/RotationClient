// IRotation.aidl
package com.app.ar.rotationservice;
import com.app.ar.rotationservice.ItaskCallback;

// Declare any non-default types here with import statements

interface IRotation {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void registerCallback (ItaskCallback CB); // Register Callback
     void unregisterCallback (ItaskCallback CB); // Unregister Callback
     void startCallback ();

}
