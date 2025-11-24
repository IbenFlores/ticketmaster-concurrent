package com.basededatosii.ticketmaster_concurrent.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

@Data
@Entity
@Table(name = "asientos")
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asiento_id")
    private Long asientoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_id", nullable = false)
    private Zona zona;

    @Column(length = 10)
    private String fila;

    @Column(name = "numero_asiento", length = 10)
    private String numeroAsiento;

    @Column(nullable = false, length = 20)
    private String estado; // 'DISPONIBLE', 'OCUPADO', 'VENDIDO'

    @Version
    private Integer version;
}