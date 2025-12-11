package nfteen.dondon.dondon.grow.repository;

import nfteen.dondon.dondon.grow.entity.Prize;
import nfteen.dondon.dondon.grow.entity.UserPrize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPrizeRepository extends JpaRepository<UserPrize, Long> {
    Optional<UserPrize> findByUserIdAndPrizeCode(Long userId, String code);
    List<UserPrize> findByUserId(Long userId);

    int updateAchieved(Long userId, String code);}
