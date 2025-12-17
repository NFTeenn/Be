package nfteen.dondon.dondon.grow.repository;

import nfteen.dondon.dondon.grow.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikesRepository extends JpaRepository<Like, Long> {
    List<Like> findByMyInfo_UserIdAndDescription(Long myInfoUserId,String description);

    boolean existsByMyInfo_UserIdAndTargetIdAndDescription(Long myInfoUserId, Long targetId, String description);

    void deleteByMyInfo_UserIdAndTargetIdAndDescription(Long myInfoUserId, Long targetId, String description);
}
