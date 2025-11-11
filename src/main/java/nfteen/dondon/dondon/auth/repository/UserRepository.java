package nfteen.dondon.dondon.auth.repository;

import nfteen.dondon.dondon.auth.entity.GoogleUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<GoogleUser, Long> {
    Optional<GoogleUser> findByEmail(String email);
}
