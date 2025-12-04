package com.basededatosii.ticketmaster_concurrent.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.basededatosii.ticketmaster_concurrent.model.Asiento;
import com.basededatosii.ticketmaster_concurrent.model.Zona;
import com.basededatosii.ticketmaster_concurrent.repository.AsientoRepository;
import com.basededatosii.ticketmaster_concurrent.repository.ZonaRepository;
import com.basededatosii.ticketmaster_concurrent.service.AsientoService;

@Service
public class AsientoServiceImpl implements AsientoService {

    private final AsientoRepository asientoRepository;
    private final ZonaRepository zonaRepository;

    public AsientoServiceImpl(AsientoRepository asientoRepository, ZonaRepository zonaRepository) {
        this.asientoRepository = asientoRepository;
        this.zonaRepository = zonaRepository;
    }

    @Override
    @Transactional
    public Asiento crearAsiento(Asiento asiento) {
        Long zonaId = asiento.getZona().getZonaId();
        
        Zona zona = zonaRepository.findById(zonaId)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con ID: " + zonaId));
        
        asiento.setZona(zona);

        if (asiento.getEstado() == null) {
            asiento.setEstado("DISPONIBLE");
        }
        
        return asientoRepository.save(asiento);
    }

    @Override
    @Transactional(readOnly = true)
    public Asiento obtenerAsientoPorId(Long id) {
        return asientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Asiento> obtenerAsientosPorZona(Long zonaId) {
        return asientoRepository.findByZona_ZonaId(zonaId);
    }
}