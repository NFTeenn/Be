package nfteen.dondon.dondon.grow.repository;

import nfteen.dondon.dondon.grow.entity.DondonInfo;
import nfteen.dondon.dondon.grow.entity.MyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DondonInfoRepository extends JpaRepository<DondonInfo, Long> {
    List<DondonInfo> findByMyInfo_UserIdAndGraduationDateIsNotNull(Long userId);

    Optional<DondonInfo> findTopByMyInfoOrderByGenDesc(MyInfo myInfo);

    Optional<DondonInfo> findByMyInfo_UserIdAndGraduationDateIsNull(Long userId);
}
