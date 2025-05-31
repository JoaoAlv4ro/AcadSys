CREATE DATABASE sistema_faculdade;
USE sistema_faculdade;

/* Tabela Curso */
CREATE TABLE Curso (
    idCurso INT AUTO_INCREMENT PRIMARY KEY,
    nomeCurso VARCHAR(100) NOT NULL,
    carga_horaria INT NOT NULL CHECK (carga_horaria >= 20),
    limite_alunos INT NOT NULL CHECK (limite_alunos >= 1),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

/* Tabela Aluno */
CREATE TABLE Aluno (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cpf CHAR(11) NOT NULL UNIQUE,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    data_nascimento DATE NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    idCurso INT NOT NULL,
    FOREIGN KEY (idCurso) REFERENCES Curso(idCurso) ON DELETE CASCADE
);
