package com.smartbiostream.presentation.sensors

interface InterfaceSensorManager {
    fun sendMeasurementsToServer()
    fun stopListening()
}