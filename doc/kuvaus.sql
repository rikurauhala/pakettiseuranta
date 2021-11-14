CREATE TABLE Asiakkaat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE);
CREATE TABLE Paketit (id INTEGER PRIMARY KEY, seurantakoodi TEXT UNIQUE, asiakas_id INTEGER REFERENCES Asiakkaat);
CREATE TABLE Paikat (id INTEGER PRIMARY KEY, nimi TEXT UNIQUE);
CREATE TABLE Tapahtumat (id INTEGER PRIMARY KEY, kuvaus TEXT, lisayshetki INTEGER, paketti_id INTEGER REFERENCES Paketit, paikka_id INTEGER REFERENCES Paikat);
CREATE INDEX idx_asiakas ON Paketit (asiakas_id);
CREATE INDEX idx_paketti ON Tapahtumat (paketti_id);
