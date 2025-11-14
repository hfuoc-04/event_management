package com.demo.demo.repository;

import com.demo.demo.entity.Register;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegisterRepository extends JpaRepository<Register, Long> {
    boolean existsByAccountIdAndEventId(long accountId, long eventId);
    Optional<Register> findByAccountIdAndEventId(long accountId, long eventId);
}
