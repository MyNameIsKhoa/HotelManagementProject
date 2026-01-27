package com.hsf302.hotelmanagementproject.repository;

import com.hsf302.hotelmanagementproject.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        if (email == null) return Optional.empty();
        TypedQuery<User> q = em.createQuery(
                "SELECT u FROM User u WHERE lower(u.email) = :email", User.class);
        q.setParameter("email", email.toLowerCase());
        try {
            return Optional.of(q.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        if (email == null) return false;
        TypedQuery<Long> q = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE lower(u.email) = :email", Long.class);
        q.setParameter("email", email.toLowerCase());
        Long count = q.getSingleResult();
        return count != null && count > 0;
    }
}
