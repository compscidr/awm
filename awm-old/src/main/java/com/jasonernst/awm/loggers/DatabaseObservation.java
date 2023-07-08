package com.jasonernst.awm.loggers;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;

@Data
@Entity(tableName = "dataobservation")
public class DatabaseObservation {

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    long id;

    @ColumnInfo(name = "uploaded")
    private boolean uploaded;

    @ColumnInfo(name = "uploadedsuccesfully")
    private boolean uploadedSucessfully;

    @ColumnInfo(name = "observation_json")
    private String observationJson;
}
