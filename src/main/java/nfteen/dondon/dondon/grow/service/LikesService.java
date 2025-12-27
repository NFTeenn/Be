package nfteen.dondon.dondon.grow.service;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.grow.dto.LikesResponse;
import nfteen.dondon.dondon.grow.entity.Like;
import nfteen.dondon.dondon.grow.entity.MyInfo;
import nfteen.dondon.dondon.grow.repository.LikesRepository;
import nfteen.dondon.dondon.grow.repository.MyInfoRepository;
import nfteen.dondon.dondon.home.entity.Word;
import nfteen.dondon.dondon.home.repository.WordRepository;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final MyInfoRepository myInfoRepository;
    private final WordRepository wordRepository;

    @Transactional
    public boolean saveLike(Long userId, int targetId) {
        Word word = wordRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("단어가 존재하지 않습니다."));

        String description = word.getDescription();

        boolean exists = likesRepository.existsByMyInfo_UserIdAndTargetIdAndDescription(
                userId, targetId, description
        );

        if (exists) {
            likesRepository.deleteByMyInfo_UserIdAndTargetIdAndDescription(
                    userId, targetId, description
            );
            return false;
        }

        MyInfo info = myInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 존재하지 않습니다."));

        Like like = Like.builder()
                .myInfo(info)
                .targetId(targetId)
                .description(word.getDescription())
                .word(word.getWord())
                .build();

        likesRepository.save(like);
        return true;
    }

    @Transactional(readOnly = true)
    public List<LikesResponse> getLikes(Long userId) {

        if (userId == null) {
            return List.of();
        }

        List<Like> list = likesRepository.findByMyInfo_UserId(userId);

        return list.stream()
                .map(l -> new LikesResponse(
                        l.getTargetId(),
                        l.getWord(),
                        l.getDescription(),
                        true
                ))
                .toList();
    }

}
