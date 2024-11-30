package com.fiec.provafinal.models;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public abstract class GenericRepositorioImpl<T, U> implements GenericRepositorio<T, U>{
    protected EntityManager entityManager;

    abstract Class<T> getMyClass();

    @Override
    public T criar(T t) {
        entityManager.getTransaction().begin();
        T t1 = entityManager.merge(t);
        entityManager.getTransaction().commit();
        return t1;

    }

    @Override
    public List<T> ler() {
        return entityManager.createQuery("select t from " + getMyClass().getSimpleName() + " t").getResultList();
    }

    @Override
    public T lerPorId(U id) {
        return entityManager.find(getMyClass(), id);
    }

    @Override
    public void atualiza(T t, U id) {
        entityManager.getTransaction().begin();
        T t1 = entityManager.find(getMyClass(), id);
        if(t1 != null){
            entityManager.merge(t);
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public void deleta(U id) {
        entityManager.getTransaction().begin();
        T t1 = entityManager.find(getMyClass(), id);
        entityManager.remove(t1);
        entityManager.getTransaction().commit();
    }
}
