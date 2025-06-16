package model;

import java.util.ArrayList;
import java.util.List;

public class Curso {
    private int idCurso;
    private String nomeCurso;
    private int cargaHoraria;
    private int limiteAlunos;
    private boolean ativo;

    // Construtor
    public Curso() {}

    public Curso(String nomeCurso, int cargaHoraria, int limiteAlunos, boolean ativo) {
        this.nomeCurso = nomeCurso;
        this.cargaHoraria = cargaHoraria;
        this.limiteAlunos = limiteAlunos;
        this.ativo = ativo;
    }


    // Getters e Setters
    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public String getNomeCurso() {
        return nomeCurso;
    }

    public void setNomeCurso(String nomeCurso) {
        this.nomeCurso = nomeCurso;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public int getLimiteAlunos() {
        return limiteAlunos;
    }

    public void setLimiteAlunos(int limiteAlunos) {
        this.limiteAlunos = limiteAlunos;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
