create table IF NOT EXISTS PLAYER_INFO
(
    LAST_PLAY_ID INTEGER,
    SORT_COLUMN  VARCHAR2(255),
    SORT_TYPE    INTEGER,
    PLAY_TYPE    INTEGER,
    ID           INTEGER auto_increment,
    constraint PLAYER_INFO_PK
    primary key (ID)
    );

create table IF NOT EXISTS SONG_INFO
(
    ID                  INTEGER auto_increment
        primary key,
    SONG_NAME           VARCHAR(255) not null,
    TITLE               VARCHAR(255),
    ARTIST              VARCHAR(255),
    ALBUM               VARCHAR(255),
    YEAR                VARCHAR(255),
    ENCODING_TYPE       VARCHAR(255),
    SAMPLE_RATE         VARCHAR(255),
    FORMAT              VARCHAR(255),
    CHANNELS            VARCHAR(255),
    BIT_RATE            VARCHAR(255),
    TRACK_LENGTH_STRING VARCHAR(255),
    TRACK_LENGTH        INTEGER,
    SONG_SIZE           VARCHAR(255),
    FILE_NAME           VARCHAR(255),
    ALBUM_ARTIST        VARCHAR(255),
    TRACK               VARCHAR(255),
    FILE_PATH           VARCHAR(255),
    CREATE_TIME         VARCHAR(255)
);

create table IF NOT EXISTS SONG_PLAYLIST
(
    ID            INTEGER auto_increment
        primary key,
    PLAYLIST_NAME VARCHAR(255) not null
);

create table IF NOT EXISTS SONG_PLAYLIST_MID
(
    PLAYLIST_ID INTEGER not null,
    SONG_ID     INTEGER not null,
    CREATE_TIME VARCHAR(255)
);

