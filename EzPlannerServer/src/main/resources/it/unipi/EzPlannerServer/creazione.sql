CREATE SCHEMA `655163`;

CREATE TABLE `655163`.`utente` (
    `username` varchar(255) NOT NULL,
    `nome` varchar(255) NOT NULL,
    `cognome` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    PRIMARY KEY (`username`)
);


CREATE TABLE `655163`.`attivita` (
    `id` int NOT NULL AUTO_INCREMENT,
    `topic` varchar(255) NOT NULL,
    `descrizione` varchar(255) NOT NULL,
    `data` date NOT NULL,
    `utente` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`utente`) REFERENCES `utente` (`username`)
);