package com.chuckcha.currencyexchange.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<S, T> {

    List<T> findAll();

    Optional<T> findByCode(S code);

    boolean delete(T entity);

    void update(T entity);

    T save(T entity);
}
