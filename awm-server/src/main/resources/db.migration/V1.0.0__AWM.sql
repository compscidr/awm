create table LocationPanacheEntity(id bigint not null, timestampUTC bigint not null, longitude double, latitude double, altitude double, accuracy float, speed float, bearing float, provider varchar(255), primary key (id));

create table BLEObservationPanacheEntity(id bigint not null, timestampUTCMillis bigint not null, locationRoomEntity bigint not null, observationType varchar(255) )