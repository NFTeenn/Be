package nfteen.dondon.dondon.grow.service;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.grow.dto.LikesResponse;
import nfteen.dondon.dondon.grow.entity.Like;
import nfteen.dondon.dondon.grow.entity.MyInfo;
import nfteen.dondon.dondon.grow.repository.LikesRepository;
import nfteen.dondon.dondon.grow.repository.MyInfoRepository;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final MyInfoRepository myInfoRepository;
    private final ListableBeanFactory listableBeanFactory;

    @Transactional
    public boolean saveLike(Long userId, Long targetId, String des) {
        boolean exists = likesRepository.existsByMyInfo_UserIdAndTargetIdAndDescription(userId,targetId, des);

        if (exists) {
            likesRepository.deleteByMyInfo_UserIdAndTargetIdAndDescription(userId, targetId, des);
            return false;
        }


        MyInfo info = myInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));

        Like like = Like.builder()
                .myInfo(info)
                .targetId(targetId)
                .description(des)
                .build();

        likesRepository.save(like);
        return true;
    }

    public List<LikesResponse> getLikes(Long userId, String des) {

        List<Like> list = likesRepository.findByMyInfo_UserIdAndDescription(userId, des);

        return list.stream()
                .map(l -> new LikesResponse(
                        l.getTargetId(),
                        l.getDescription(),
                        true
                ))
                .toList();
    }

}
