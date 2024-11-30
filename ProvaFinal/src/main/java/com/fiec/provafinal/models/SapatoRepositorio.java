package com.fiec.provafinal.models;

import jakarta.persistence.EntityManager;

public class SapatoRepositorio extends GenericRepositorioImpl<Sapato, String> {

    public SapatoRepositorio(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    Class<Sapato> getMyClass() {
        return Sapato.class;
    }
}
