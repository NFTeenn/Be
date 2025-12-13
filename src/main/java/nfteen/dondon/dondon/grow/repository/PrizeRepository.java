package nfteen.dondon.dondon.grow.repository;

import nfteen.dondon.dondon.grow.entity.Prize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrizeRepository extends JpaRepository<Prize, Long> {
    Optional<Prize> findByCode(String code);
}
