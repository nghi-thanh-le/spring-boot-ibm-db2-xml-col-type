package fi.tuni.resourcedescription.repository.security;

import fi.tuni.resourcedescription.model.user.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken getByUserId(Integer userId);
    boolean existsByUserId(Integer userId);

    @Modifying
    void deleteByUserId(Integer userId);
}
