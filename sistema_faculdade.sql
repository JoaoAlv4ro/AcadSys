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

INSERT INTO Curso (nome_Curso, carga_horaria, limite_alunos)
VALUES 
('Engenharia da Computação', 3600, 50),
('Administração', 3000, 40),
('Direito', 3500, 60),
('Psicologia', 3200, 45),
('Medicina', 8000, 100);

INSERT INTO Aluno (cpf, nome_Aluno, email, data_nascimento, id_Curso)
VALUES 
('12345678901', 'Ana Beatriz Silva', 'ana.silva@email.com', '2000-05-10', 1),
('23456789012', 'Bruno Carvalho', 'bruno.c@email.com', '1999-03-22', 2),
('34567890123', 'Carlos Mendes', 'carlos.m@email.com', '2001-07-14', 3),
('45678901234', 'Daniela Souza', 'daniela.s@email.com', '2000-12-01', 4),
('56789012345', 'Eduardo Lima', 'eduardo.l@email.com', '1998-11-30', 5),
('67890123456', 'Fernanda Rocha', 'fernanda.r@email.com', '2002-02-15', 1),
('78901234567', 'Gustavo Martins', 'gustavo.m@email.com', '2001-08-19', 2),
('89012345678', 'Helena Duarte', 'helena.d@email.com', '1999-09-25', 3),
('90123456789', 'Igor Nunes', 'igor.n@email.com', '2000-01-05', 4),
('01234567890', 'Juliana Alves', 'juliana.a@email.com', '2002-06-30', 5);
