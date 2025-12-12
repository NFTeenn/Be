package nfteen.dondon.dondon.grow.repository;

import nfteen.dondon.dondon.grow.entity.Like;
import nfteen.dondon.dondon.grow.entity.TypeName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Like, Long> {
    List<Like> findByMyInfo_UserId(Long userId);

    List<Like> findByMyInfo_UserIdAndType(Long myInfoUserId, TypeName type);

    Optional<Like> findByMyInfo_UserIdAndTargetId(Long userId, Long targetId);

    Optional<Like>findByMyInfo_UserIdAndTargetIdAndType(Long userId, Long targetId, TypeName type);

    boolean existsByMyInfo_UserIdAndTargetIdAndType(Long userId, Long targetId, TypeName type);

    void deleteByMyInfo_UserIdAndTargetIdAndType(Long userId, Long targetId, TypeName type);
}
