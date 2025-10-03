package com.alfredamos.springmanagementofemployees.repositories;

import com.alfredamos.springmanagementofemployees.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.user.id = :userId AND t.expired = true AND t.revoked = true")
    void deleteAllInvalidTokensByUserId(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE  t.expired = true AND t.revoked = true")
    void deleteAllInvalidTokens();

    @Query("""
    select t from Token t inner join User u on t.user.id = u.id
    where u.id =:userId and (t.expired = false  or t.revoked = false )
    """)
    List<Token> findAllValidTokensByUser(UUID userId);
    Optional<Token> findByAccessToken(String token);
    Optional<Token> findByRefreshToken(String refreshToken);
}
