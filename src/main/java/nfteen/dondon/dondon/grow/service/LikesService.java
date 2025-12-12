package nfteen.dondon.dondon.grow.service;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.grow.dto.LikeResponse;
import nfteen.dondon.dondon.grow.dto.LikesResponse;
import nfteen.dondon.dondon.grow.entity.Like;
import nfteen.dondon.dondon.grow.entity.MyInfo;
import nfteen.dondon.dondon.grow.entity.TypeName;
import nfteen.dondon.dondon.grow.repository.LikesRepository;
import nfteen.dondon.dondon.grow.repository.MyInfoRepository;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final MyInfoRepository myInfoRepository;
    private final ListableBeanFactory listableBeanFactory;

    @Transactional
    public boolean saveLike(Long userId, Long targetId, TypeName type) {
        boolean exists = likesRepository.existsByMyInfo_UserIdAndTargetIdAndType(userId,targetId,type);

        if (exists) {
            likesRepository.deleteByMyInfo_UserIdAndTargetIdAndType(userId, targetId, type);
            return false;
        }


        MyInfo info = myInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));


        Like like = Like.builder()
                .myInfo(info)
                .targetId(targetId)
                .type(type)
                .build();
        try{
            likesRepository.save(like);
            return true;
        } catch (DataIntegrityViolationException e) {
            likesRepository.deleteByMyInfo_UserIdAndTargetIdAndType(userId, targetId, type);
            return false;
        }
    }

    public List<LikesResponse> getLikes(Long userId, TypeName type, Long targetId) {

        if(targetId != null){
            Optional<Like> like = (type == null)
                    ? likesRepository.findByMyInfo_UserIdAndTargetId(userId, targetId)
                    : likesRepository.findByMyInfo_UserIdAndTargetIdAndType(userId, targetId, type);

            return List.of(
                    like.map(l -> new LikesResponse(l.getTargetId(), l.getType().name(), true))
                            .orElse(new LikesResponse(targetId, type != null ? type.name() : null , false))
            );
        }

        List<Like> list;

        if(type == null) {
            list = likesRepository.findByMyInfo_UserId(userId);
        } else{
            list = likesRepository.findByMyInfo_UserIdAndType(userId, type);
        }

        return list.stream()
                .map(l -> new LikesResponse(l.getTargetId(), l.getType().name(), true))
                .toList();
    }

}
