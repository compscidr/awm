package com.github.compscidr.awm.db.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jasonernst.awm_common.db.Observation
import com.github.compscidr.awm.db.ObservationRepository
import com.jasonernst.awm_common.db.ObservationType
import org.slf4j.LoggerFactory

class RoomObservationRepository(private val daoMap: Map<ObservationType, ObservationDao<*>>, private val unsyncedLimit: Int = 1000):
    ObservationRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

    private var previousOldestEntry: ObservationRoomEntity? = null

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

    private val oldestObservationRoomEntity = MediatorLiveData<ObservationRoomEntity?>().apply {
        fun update(observationRoomEntity: ObservationRoomEntity?) {
            if (observationRoomEntity != null) {
                logger.debug("OLDEST ENTITY: $observationRoomEntity")
                if (observationRoomEntity is BLEObservationRoomEntity) {
                    logger.debug("BLE ENTITY: $observationRoomEntity")
                }
                val oldestTimestamp = previousOldestEntry?.timestampUTCMillis ?: Long.MAX_VALUE
                if (observationRoomEntity.timestampUTCMillis < oldestTimestamp) {
                    previousOldestEntry = observationRoomEntity
                    value = observationRoomEntity
                } else {
                    logger.debug("Not updating oldest, new: $observationRoomEntity, old: $previousOldestEntry")
                }
            }
        }

        for (dao in daoMap.values) {
            addSource(dao.getOldest()) {
                logger.error("FIREEEEDD")
                update(it as ObservationRoomEntity?)
            }
        }
    }

    private val oldestObservation = MediatorLiveData<Observation?>().apply {
        addSource(oldestObservationRoomEntity) {
            if (it != null) {
                value = ObservationRoomEntity.toObservation(it)
            }
        }
    }

    override fun insert(observation: Observation) {
        while ((getNumObservations().value?.plus(1) ?: 1) > unsyncedLimit) {
            logger.debug("can't insert, too many entries, finding oldest to delete")
            val entryToDelete = oldestObservationRoomEntity.value
            if (entryToDelete != null) {
                logger.debug("oldest to delete: $entryToDelete")
                if (entryToDelete.observationType == ObservationType.BLE) {
                    val dao = daoMap[entryToDelete.observationType] as BluetoothDao
                    dao.delete(entryToDelete as BLEObservationRoomEntity)
                    logger.debug("deleted: $entryToDelete")
                }
                // todo keep track of a count of deleted entries that never got synced
            } else {
                logger.error("Couldn't find oldest to delete")
            }
        }

        if (observation.observationType == ObservationType.BLE) {
            val dao = daoMap[observation.observationType] as BluetoothDao
            val observationRoomEntity = ObservationRoomEntity.fromObservation(observation)
            dao.insert(observationRoomEntity as BLEObservationRoomEntity)
        }
    }

    private fun delete(observationRoomEntity: ObservationRoomEntity) {
        // if we don't do this, we won't be able to delete the next oldest entry
        if (observationRoomEntity.timestampUTCMillis == previousOldestEntry?.timestampUTCMillis) {
            previousOldestEntry = null
        }

        if (observationRoomEntity.observationType == ObservationType.BLE) {
            val dao = daoMap[observationRoomEntity.observationType] as BluetoothDao
            val entity = observationRoomEntity as BLEObservationRoomEntity
            logger.debug("Trying to delete observation: $entity")
            val result = dao.delete(entity)
            logger.debug("Deleted observation: $entity, result: $result")
        } else {
            logger.error("Unknown observation type")
        }
    }

    override fun delete(observation: Observation) {
        val observationRoomEntity = ObservationRoomEntity.fromObservation(observation)
        delete(observationRoomEntity)
    }

    override fun getNumObservations(): LiveData<Int> {
        return numObservations
    }

    override fun getOldestObservation(): LiveData<Observation?> {
        return oldestObservation
    }
}