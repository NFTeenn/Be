package nfteen.dondon.dondon.grow.repository;

import nfteen.dondon.dondon.grow.entity.MyInfo;
import nfteen.dondon.dondon.grow.entity.UserAcc;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccRepository extends JpaRepository<UserAcc, Long> {
    Optional<UserAcc> findByMyInfoAndEquippedTrue(MyInfo myInfo);

    Optional<UserAcc> findByMyInfoAndAccId(MyInfo myInfo, Long accId);
}
