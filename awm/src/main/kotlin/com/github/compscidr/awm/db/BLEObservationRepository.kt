package com.github.compscidr.awm.db

import androidx.lifecycle.LiveData
import org.slf4j.LoggerFactory

class BLEObservationRepository(private val observationDao: BLEObservationDao, private val unsyncedLimit: Int = 1000) {
    val logger = LoggerFactory.getLogger(javaClass)
    val unsycnedEntries: LiveData<Int> = observationDao.getNumEntries()

    fun insert(observationEntity: ObservationEntity) {
        while (observationDao.size() + 1 > unsyncedLimit) {
            val entryToDelete = observationDao.getOldest()
            if (entryToDelete != null) {
                //logger.debug("Deleting oldest")
                observationDao.delete(entryToDelete)
                // todo keep track of a count of deleted entries that never got synced
            } else {
                logger.error("Couldn't find oldest to delete")
            }
        }
        observationDao.insert(observationEntity as BLEObservationEntity)
    }

    fun getOldest(): BLEObservationEntity? {
        return observationDao.getOldest()
    }

    fun delete(observationEntity: ObservationEntity) {
        observationDao.delete(observationEntity as BLEObservationEntity)
    }
}