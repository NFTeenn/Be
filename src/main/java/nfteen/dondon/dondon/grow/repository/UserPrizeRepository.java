package nfteen.dondon.dondon.grow.repository;

import nfteen.dondon.dondon.grow.entity.Prize;
import nfteen.dondon.dondon.grow.entity.UserPrize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserPrizeRepository extends JpaRepository<UserPrize, Long> {
    Optional<UserPrize> findByUserIdAndPrizeCode(Long userId, String code);
    List<UserPrize> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE UserPrize u SET u.achieved = true WHERE u.user.id = :userId AND u.prize.code = :code")
    int updateAchieved(Long userId, String code);}
