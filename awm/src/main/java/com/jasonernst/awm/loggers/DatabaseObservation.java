package com.jasonernst.awm.loggers;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "dataobservation")
public class DatabaseObservation {

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    long id;

    @Getter @Setter @ColumnInfo(name = "uploaded")
    private boolean uploaded;

    @Getter @Setter @ColumnInfo(name = "uploadedsuccesfully")
    private boolean uploadedSucessfully;

    @Getter @Setter @ColumnInfo(name = "observation_json")
    private String observationJson;
}
