package nfteen.dondon.dondon.grow.repository;

import nfteen.dondon.dondon.grow.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikesRepository extends JpaRepository<Like, Long> {
    List<Like> findByMyInfo_UserId(Long myInfoUserId);

    boolean existsByMyInfo_UserIdAndTargetIdAndDescription(Long myInfoUserId, int targetId, String description);

    void deleteByMyInfo_UserIdAndTargetIdAndDescription(Long myInfoUserId, int targetId, String description);
}
