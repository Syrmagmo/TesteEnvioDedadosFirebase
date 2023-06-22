package com.example.testeenviodedadosfirebase.model;

public class livroModel {
    private String LivroTitulo, LivroAutor, LivroAno, LivroGenero, LivroEstado, LivroDescricao, LivroLongitude, LivroLatitude, LivroImage, LivroId, UsuarioAtualID;

    public livroModel() {
    }

    public livroModel(String livroTitulo, String livroAutor, String livroAno,
                      String livroGenero, String livroEstado, String livroDescricao,
                      String livroLongitude, String livroLatitude, String livroImage,
                      String livroId, String usuarioAtualID) {
        LivroTitulo = livroTitulo;
        LivroAutor = livroAutor;
        LivroAno = livroAno;
        LivroGenero = livroGenero;
        LivroEstado = livroEstado;
        LivroDescricao = livroDescricao;
        LivroLongitude = livroLongitude;
        LivroLatitude = livroLatitude;
        LivroImage = livroImage;
        LivroId = livroId;
        UsuarioAtualID = usuarioAtualID;
    }

    public String getLivroTitulo() {
        return LivroTitulo;
    }

    public void setLivroTitulo(String livroTitulo) {
        LivroTitulo = livroTitulo;
    }

    public String getLivroAutor() {
        return LivroAutor;
    }

    public void setLivroAutor(String livroAutor) {
        LivroAutor = livroAutor;
    }

    public String getLivroAno() {
        return LivroAno;
    }

    public void setLivroAno(String livroAno) {
        LivroAno = livroAno;
    }

    public String getLivroGenero() {
        return LivroGenero;
    }

    public void setLivroGenero(String livroGenero) {
        LivroGenero = livroGenero;
    }

    public String getLivroEstado() {
        return LivroEstado;
    }

    public void setLivroEstado(String livroEstado) {
        LivroEstado = livroEstado;
    }

    public String getLivroDescricao() {
        return LivroDescricao;
    }

    public void setLivroDescricao(String livroDescricao) {
        LivroDescricao = livroDescricao;
    }

    public String getLivroLongitude() {
        return LivroLongitude;
    }

    public void setLivroLongitude(String livroLongitude) {
        LivroLongitude = livroLongitude;
    }

    public String getLivroLatitude() {
        return LivroLatitude;
    }

    public void setLivroLatitude(String livroLatitude) {
        LivroLatitude = livroLatitude;
    }

    public String getLivroImage() {
        return LivroImage;
    }

    public void setLivroImage(String livroImage) {
        LivroImage = livroImage;
    }

    public String getLivroId() {
        return LivroId;
    }

    public void setLivroId(String livroId) {
        LivroId = livroId;
    }

    public String getUsuarioAtualID() {
        return UsuarioAtualID;
    }

    public void setUsuarioAtualID(String usuarioAtualID) {
        UsuarioAtualID = usuarioAtualID;
    }
}
