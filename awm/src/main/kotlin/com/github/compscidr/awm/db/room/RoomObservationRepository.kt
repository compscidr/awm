package com.github.compscidr.awm.db.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.github.compscidr.awm.db.Observation
import com.github.compscidr.awm.db.ObservationRepository
import com.github.compscidr.awm.db.ObservationType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

class RoomObservationRepository(private val daoMap: Map<ObservationType, ObservationDao<*>>, private val unsyncedLimit: Int = 1000): ObservationRepository {

    val logger = LoggerFactory.getLogger(javaClass)
    val unsycnedEntries: LiveData<Int> = MediatorLiveData<Int>().apply {
        fun update() {
            val tempValue = daoMap.values.sumOf { it.getNumEntries().value ?: 0 }
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    value = tempValue
                }
            }
        }
        update()
    }

    val oldestObservationEntity: LiveData<ObservationEntity?> = MediatorLiveData<ObservationEntity?>().apply {
        fun update() {
            val tempValue = daoMap.values.mapNotNull { it.getOldest() }.minByOrNull { it.timestampUTCMillis }
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    value = tempValue
                }
            }
        }
        update()
    }

    fun insert(observationEntity: ObservationEntity) {
        while ((unsycnedEntries.value?.plus(1) ?: 1) > unsyncedLimit) {
            val entryToDelete = oldestObservationEntity.value
            if (entryToDelete != null) {
                daoMap[entryToDelete.observationType]?.delete(entryToDelete)
                // todo keep track of a count of deleted entries that never got synced
            } else {
                logger.error("Couldn't find oldest to delete")
            }
        }
        daoMap[observationEntity.observationType]?.insert(observationEntity)
    }

    fun getOldest(): ObservationEntity? {
        return oldestObservationEntity.value
    }

    fun delete(observationEntity: ObservationEntity) {
        daoMap[observationEntity.observationType]?.delete(observationEntity)
    }

    override fun insert(observation: Observation) {
        val observationEntity = ObservationEntity.fromObservation(observation)
        daoMap[observationEntity.observationType]?.insert(observationEntity)
    }

    override fun delete(observation: Observation) {
        val observationEntity = ObservationEntity.fromObservation(observation)
        delete(observationEntity)
    }

    override fun getNumObservations(observationType: ObservationType): LiveData<Int> {
        return daoMap[observationType]?.getNumEntries() ?: throw RuntimeException("Unknown observation type")
    }

    override fun getNumObservations(): LiveData<Int> {
        return unsycnedEntries
    }

    override fun getOldestObservation(observationType: ObservationType): Observation? {
        return daoMap[observationType]?.getOldest()?.let { ObservationEntity.toObservation(it) }
    }

    override fun getOldestObservation(): Observation? {
        return oldestObservationEntity.value?.let { ObservationEntity.toObservation(it) }
    }
}