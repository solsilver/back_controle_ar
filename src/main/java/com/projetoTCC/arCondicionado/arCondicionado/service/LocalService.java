package com.projetoTCC.arCondicionado.arCondicionado.service;

import com.projetoTCC.arCondicionado.arCondicionado.model.Local;
import com.projetoTCC.arCondicionado.arCondicionado.repository.LocalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocalService {
    private final LocalRepository repository;

    public LocalService(LocalRepository repository) {
        this.repository = repository;
    }

    public List<Local> buscarLocais(Long localId) {
        return repository.findAll();
    }
}
