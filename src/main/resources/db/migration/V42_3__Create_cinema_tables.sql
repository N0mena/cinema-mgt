create table if not exists "user"
(
    id         uuid primary key,
    first_name varchar,
    last_name  varchar not null,
    birthdate  date    not null,
    email      varchar not null unique,
    password   varchar,
    phone      varchar,
    role       varchar
);

create table if not exists movies
(
    id          uuid primary key,
    title       varchar      not null,
    genre       varchar,
    description varchar(2000),
    duration    interval
);

create table if not exists rooms
(
    id       uuid primary key,
    number   varchar not null unique,
    capacity integer not null
);

create table if not exists seats
(
    id      uuid primary key,
    number  varchar not null,
    room_id uuid    not null references rooms (id),
    unique (room_id, number)
);

create table if not exists projections
(
    id         uuid primary key,
    datetime   timestamptz     not null,
    seat_price numeric(10, 2) not null,
    movie_id   uuid            not null references movies (id),
    room_id    uuid            not null references rooms (id)
);

create table if not exists reservations
(
    id            uuid primary key,
    user_id       uuid      not null references "user" (id),
    projection_id uuid      not null references projections (id),
    status        varchar   not null,
    reserved_at   timestamp not null
);

create table if not exists reservation_seats
(
    reservation_id uuid not null references reservations (id),
    seat_id        uuid not null references seats (id),
    primary key (reservation_id, seat_id)
);

create  table if not exists receipts(
    id uuid primary key,
    reservation_id integer references reservations (id),
    createdAt timestamp deflaut now(),
    file_path varchar(250)
);
