package com.example.sweettemptation.dto;

import java.io.Serializable;

public class UsuarioResponse implements Serializable {
    private int id;
    private String usuario;
    private String nombre;
    private String apellidos;
    private String correo;
    private String telefono;
    private String direccion;
    private int idRol;

    public UsuarioResponse() {
    }

    public UsuarioResponse(int id, String usuario, String nombre, String apellidos, 
                          String correo, String telefono, String direccion, int idRol) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.telefono = telefono;
        this.direccion = direccion;
        this.idRol = idRol;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        switch (idRol) {
            case 1: return "Administrador";
            case 2: return "Empleado";
            case 3: return "Cliente";
            default: return "Desconocido";
        }
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }
}



