// ItaskCallback.aidl
package com.app.ar.rotationservice;

// Declare any non-default types here with import statements

interface ItaskCallback {
    void valueChanged(float pitch,float roll);
}