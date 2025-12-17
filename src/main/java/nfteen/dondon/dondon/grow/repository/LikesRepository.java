package nfteen.dondon.dondon.grow.repository;

import nfteen.dondon.dondon.grow.entity.Like;
import nfteen.dondon.dondon.grow.entity.TypeName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Like, Long> {
    List<Like> findByMyInfo_UserIdAndTypeAndDescription(Long myInfoUserId, TypeName type, String description);

    boolean existsByMyInfo_UserIdAndTargetIdAndTypeAndDescription(Long myInfoUserId, Long targetId, TypeName type, String description);

    void deleteByMyInfo_UserIdAndTargetIdAndTypeAndDescription(Long myInfoUserId, Long targetId, TypeName type, String description);
}
