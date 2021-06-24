drop database if exists `kg`;
create database `kg`;
use kg;
drop table if exists `User`;
create table User
(
    id       bigint auto_increment
        primary key,
    username char(255)                                                                            not null,
    password char(255)                                                                            not null,
    email    char(255)                                                                            not null,
    imgUrl   varchar(255) default 'https://niocoin-181.oss-cn-shanghai.aliyuncs.com/default.jpeg' not null
);

INSERT INTO kg.User (id, username, password, email, imgUrl) VALUES (1, '123', 'Mt8eNaUy21cwj1e+xNGrUg==', '123@qq.com', 'https://niocoin-181.oss-cn-shanghai.aliyuncs.com/default.jpeg');

drop table if exists `userGraph`;
create table userGraph
(
    userId  bigint               not null,
    graphId bigint               not null,
    owned   int        default 1 not null,
    starred tinyint(1) default 0 not null
);

INSERT INTO kg.userGraph (userId, graphId, owned, starred)
VALUES (1, 1, 1, 1);

drop table if exists `Graph`;
create table Graph
(
    graphId   bigint                                                                             not null
        primary key,
    graphName varchar(255)                                                                       not null,
    time      timestamp                                                                          not null,
    imgURL    varchar(255) default 'https://niocoin-181.oss-cn-shanghai.aliyuncs.com/home-ele.png' not null,
    public    tinyint(1)   default 0                                                             not null,
    status    varchar(20)  default 'Created'                                                     not null
);

INSERT INTO kg.Graph (graphId, graphName, time, imgURL, public, status) VALUES (1, 'test', '2021-05-16 21:58:25', 'https://niocoin-181.oss-cn-shanghai.aliyuncs.com/home-ele.png', 0, 'Created');
