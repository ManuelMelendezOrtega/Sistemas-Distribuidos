package com.sistemasdistr.basico.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

// Modelo que representa los torneos disponibles en la aplicación.
// Guarda los detalles del evento, incluyendo su fecha exacta y las coordenadas geográficas para su ubicación.
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "torneo")
public class Torneo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 250)
    private String descripcion;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    @Column(name = "fecha")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
}