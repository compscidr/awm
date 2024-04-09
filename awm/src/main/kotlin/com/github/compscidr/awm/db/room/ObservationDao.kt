package com.github.compscidr.awm.db.room

import androidx.lifecycle.LiveData

interface ObservationDao<T> {
    fun getNumEntries(): LiveData<Int>
    fun getOldest(): LiveData<T?>
}