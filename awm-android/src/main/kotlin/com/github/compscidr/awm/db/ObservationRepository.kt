package com.github.compscidr.awm.db

import androidx.lifecycle.LiveData
import com.jasonernst.awm_common.db.Observation

interface ObservationRepository {
    fun insert(observation: Observation)
    fun delete(observation: Observation)
    fun getNumObservations(): LiveData<Int>
    fun getOldestObservation(): LiveData<Observation?>
}