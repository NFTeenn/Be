package nfteen.dondon.dondon.grow.repository;

import nfteen.dondon.dondon.grow.entity.MyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyInfoRepository extends JpaRepository<MyInfo, Long> {
    Optional<MyInfo> findByUserId(Long userId);

}
