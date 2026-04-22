package com.example.restservice.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.restservice.model.user.Specialite;

@Repository
public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {
    
}
