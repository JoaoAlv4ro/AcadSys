CREATE DATABASE sistema_faculdade;
USE sistema_faculdade;

/* Tabela Curso */
CREATE TABLE Curso (
    id_Curso INT AUTO_INCREMENT PRIMARY KEY,
    nome_Curso VARCHAR(100) NOT NULL,
    carga_horaria INT NOT NULL CHECK (carga_horaria >= 20),
    limite_alunos INT NOT NULL CHECK (limite_alunos >= 1),
    ativo_Curso BOOLEAN NOT NULL DEFAULT TRUE
);

/* Tabela Aluno */
CREATE TABLE Aluno (
    id_Aluno INT AUTO_INCREMENT PRIMARY KEY,
    cpf CHAR(11) NOT NULL UNIQUE,
    nome_Aluno VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    data_nascimento DATE NOT NULL,
    ativo_Aluno BOOLEAN NOT NULL DEFAULT TRUE,
    id_Curso INT NOT NULL,
    FOREIGN KEY (id_Curso) REFERENCES Curso(id_Curso) ON DELETE CASCADE
);
