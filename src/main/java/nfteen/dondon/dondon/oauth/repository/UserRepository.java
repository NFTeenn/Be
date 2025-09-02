package nfteen.dondon.dondon.oauth.repository;

import nfteen.dondon.dondon.oauth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);
}