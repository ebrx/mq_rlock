create table public.mq_test (
                                id integer primary key not null default nextval('your_entity_id_seq'::regclass),
                                status character varying(8),
                                start_time timestamp without time zone,
                                end_time timestamp without time zone
);

