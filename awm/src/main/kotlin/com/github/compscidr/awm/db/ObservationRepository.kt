package com.github.compscidr.awm.db

import androidx.lifecycle.LiveData

interface ObservationRepository {
    fun insert(observation: Observation)
    fun delete(observation: Observation)
    fun getNumObservations(observationType: ObservationType): LiveData<Int>
    fun getNumObservations(): LiveData<Int>
    fun getOldestObservation(observationType: ObservationType): Observation?
    fun getOldestObservation(): Observation?
}