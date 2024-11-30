package com.fiec.provafinal.models;

import java.util.List;

public interface GenericRepositorio<T, U> {

    T criar(T pessoa);
    List<T> ler();
    T lerPorId(U id);
    void atualiza(T pessoa, U id);
    void deleta(U id);

}
