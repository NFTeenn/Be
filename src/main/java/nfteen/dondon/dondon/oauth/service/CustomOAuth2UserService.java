

package nfteen.dondon.dondon.oauth.service;

import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.oauth.dto.CustomOAuth2User;
import nfteen.dondon.dondon.oauth.dto.GoogleResponse;
import nfteen.dondon.dondon.oauth.dto.OAuth2Response;
import nfteen.dondon.dondon.oauth.dto.UserDTO;
import nfteen.dondon.dondon.oauth.entity.UserEntity;
import nfteen.dondon.dondon.oauth.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response;

        if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());

        } else {

            throw new OAuth2AuthenticationException("Unsupported OAuth2 provider:  " + registrationId);
        }

        UserEntity userEntity = userRepository.findByEmail(oAuth2Response.getEmail())
                .orElseGet(()->{
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(oAuth2Response.getEmail());
                    newUser.setName(oAuth2Response.getName());
                    newUser.setProvider(oAuth2Response.getProvider());
                    newUser.setProviderId(oAuth2Response.getProviderId());
                    newUser.setRole("ROLE_USER");
                    return userRepository.save(newUser);
                });



        userEntity.setName(oAuth2Response.getName());
        userRepository.save(userEntity);

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setName(userEntity.getName());
        userDTO.setRole(userEntity.getRole());

        return new CustomOAuth2User(userDTO);
    }
}

