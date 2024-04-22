package com.capstone.cinemate.Member.repository;

import com.capstone.cinemate.Member.domain.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthEntity, Long> {
    Optional<AuthEntity> findByMemberId(Long memberId);
}
