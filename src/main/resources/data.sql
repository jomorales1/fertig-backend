INSERT INTO mydb.sonido (id_sonido)
SELECT * FROM (SELECT "belonging") AS tmp
WHERE NOT EXISTS (
	SELECT id_sonido FROM mydb.sonido WHERE id_sonido = "belonging"
) LIMIT 1;

INSERT INTO mydb.sonido (id_sonido)
SELECT * FROM (SELECT "chilled") AS tmp
WHERE NOT EXISTS (
	SELECT id_sonido FROM mydb.sonido WHERE id_sonido = "chilled"
) LIMIT 1;

INSERT INTO mydb.sonido (id_sonido)
SELECT * FROM (SELECT "dusk") AS tmp
WHERE NOT EXISTS (
	SELECT id_sonido FROM mydb.sonido WHERE id_sonido = "dusk"
) LIMIT 1;

INSERT INTO mydb.sonido (id_sonido)
SELECT * FROM (SELECT "staring") AS tmp
WHERE NOT EXISTS (
	SELECT id_sonido FROM mydb.sonido WHERE id_sonido = "staring"
) LIMIT 1;

INSERT INTO mydb.sonido (id_sonido)
SELECT * FROM (SELECT "theworld") AS tmp
WHERE NOT EXISTS (
	SELECT id_sonido FROM mydb.sonido WHERE id_sonido = "theworld"
) LIMIT 1;