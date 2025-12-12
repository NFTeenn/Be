package nfteen.dondon.dondon.grow.repository;

import nfteen.dondon.dondon.grow.entity.Accessary;
import nfteen.dondon.dondon.grow.entity.MyInfo;
import nfteen.dondon.dondon.grow.entity.UserAcc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccRepository extends JpaRepository<UserAcc, Long> {

    UserAcc findByMyInfoAndEquippedTrue(MyInfo myInfo);
    boolean existsByMyInfoAndAcc(MyInfo myInfo, Accessary acc);

}
