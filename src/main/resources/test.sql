CREATE TABLE test (
    id INTEGER,
    val VARCHAR(40)
);

INSERT INTO TEST values (1,'test');
CREATE USER mlab WITH PASSWORD '1324';
GRANT ALL PRIVILEGES ON TABLE test TO mlab;


SELECT val FROM test where id = 1