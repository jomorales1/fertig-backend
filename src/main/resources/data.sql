INSERT INTO mydb.sonido (id_sonido)
SELECT * FROM (SELECT "classical") AS tmp
WHERE NOT EXISTS (
	SELECT id_sonido FROM mydb.sonido WHERE id_sonido = "classical"
) LIMIT 1;

INSERT INTO mydb.sonido (id_sonido)
SELECT * FROM (SELECT "nature") AS tmp
WHERE NOT EXISTS (
	SELECT id_sonido FROM mydb.sonido WHERE id_sonido = "nature"
) LIMIT 1;

INSERT INTO mydb.sonido (id_sonido)
SELECT * FROM (SELECT "electronic") AS tmp
WHERE NOT EXISTS (
	SELECT id_sonido FROM mydb.sonido WHERE id_sonido = "electronic"
) LIMIT 1;

INSERT INTO mydb.sonido (id_sonido)
SELECT * FROM (SELECT "rain") AS tmp
WHERE NOT EXISTS (
	SELECT id_sonido FROM mydb.sonido WHERE id_sonido = "rain"
) LIMIT 1;