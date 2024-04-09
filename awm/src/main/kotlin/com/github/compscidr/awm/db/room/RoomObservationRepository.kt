package com.github.compscidr.awm.db.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.github.compscidr.awm.db.Observation
import com.github.compscidr.awm.db.ObservationRepository
import com.github.compscidr.awm.db.ObservationType
import org.slf4j.LoggerFactory

class RoomObservationRepository(private val daoMap: Map<ObservationType, ObservationDao<*>>, private val unsyncedLimit: Int = 1000): ObservationRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    private var previousOldestEntry: ObservationEntity? = null

    private val numObservations = MediatorLiveData<Int>().apply {
        val latestValues = mutableMapOf<ObservationType, Int>()
        fun update(observationType: ObservationType, latestValue: Int) {
            latestValues[observationType] = latestValue
            value = latestValues.values.sum()
        }

        for (daoEntry in daoMap) {
            val daoType = daoEntry.key
            val dao = daoEntry.value
            addSource(dao.getNumEntries()) { update(daoType, it) }
        }
    }

    private val oldestObservationEntity = MediatorLiveData<ObservationEntity?>().apply {
        fun update(observationEntity: ObservationEntity?) {
            if (observationEntity != null) {
                logger.debug("OLDEST ENTITY: $observationEntity")
                if (observationEntity is BLEObservationEntity) {
                    logger.debug("BLE ENTITY: $observationEntity")
                }
                val oldestTimestamp = previousOldestEntry?.timestampUTCMillis ?: Long.MAX_VALUE
                if (observationEntity.timestampUTCMillis < oldestTimestamp) {
                    previousOldestEntry = observationEntity
                    value = observationEntity
                } else {
                    logger.debug("Not updating oldest, new: $observationEntity, old: $previousOldestEntry")
                }
            }
        }

        for (dao in daoMap.values) {
            addSource(dao.getOldest()) {
                logger.error("FIREEEEDD")
                update(it as ObservationEntity?)
            }
        }
    }

    private val oldestObservation = MediatorLiveData<Observation?>().apply {
        addSource(oldestObservationEntity) {
            if (it != null) {
                value = ObservationEntity.toObservation(it)
            }
        }
    }

    override fun insert(observation: Observation) {
        while ((getNumObservations().value?.plus(1) ?: 1) > unsyncedLimit) {
            logger.debug("can't insert, too many entries, finding oldest to delete")
            val entryToDelete = oldestObservationEntity.value
            if (entryToDelete != null) {
                logger.debug("oldest to delete: $entryToDelete")
                if (entryToDelete.observationType == ObservationType.BLE) {
                    val dao = daoMap[entryToDelete.observationType] as BluetoothDao
                    dao.delete(entryToDelete as BLEObservationEntity)
                    logger.debug("deleted: $entryToDelete")
                }
                // todo keep track of a count of deleted entries that never got synced
            } else {
                logger.error("Couldn't find oldest to delete")
            }
        }

        if (observation.observationType == ObservationType.BLE) {
            val dao = daoMap[observation.observationType] as BluetoothDao
            val observationEntity = ObservationEntity.fromObservation(observation)
            dao.insert(observationEntity as BLEObservationEntity)
        }
    }

    private fun delete(observationEntity: ObservationEntity) {
        // if we don't do this, we won't be able to delete the next oldest entry
        if (observationEntity.timestampUTCMillis == previousOldestEntry?.timestampUTCMillis) {
            previousOldestEntry = null
        }

        if (observationEntity.observationType == ObservationType.BLE) {
            val dao = daoMap[observationEntity.observationType] as BluetoothDao
            val entity = observationEntity as BLEObservationEntity
            logger.debug("Trying to delete observation: $entity")
            val result = dao.delete(entity)
            logger.debug("Deleted observation: $entity, result: $result")
        } else {
            logger.error("Unknown observation type")
        }
    }

    override fun delete(observation: Observation) {
        val observationEntity = ObservationEntity.fromObservation(observation)
        delete(observationEntity)
    }

    override fun getNumObservations(): LiveData<Int> {
        return numObservations
    }

    override fun getOldestObservation(): LiveData<Observation?> {
        return oldestObservation
    }
}