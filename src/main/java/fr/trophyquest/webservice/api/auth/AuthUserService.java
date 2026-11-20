package fr.trophyquest.webservice.api.auth;

import fr.trophyquest.webservice.domain.entity.User;
import fr.trophyquest.webservice.domain.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthUserService {

    private final UserRepository userRepository;
    private final AuthUserMapper authUserMapper;

    public AuthUserService(
            UserRepository userRepository,
            AuthUserMapper authUserMapper
    ) {
        this.userRepository = userRepository;
        this.authUserMapper = authUserMapper;
    }

    public AuthUserResponse fetchCurrentUser(CognitoUserInfo cognitoUserInfo, boolean isAdmin) {
        User user = findOrCreate(cognitoUserInfo);
        return authUserMapper.toDTO(user, cognitoUserInfo.preferredUsername(), isAdmin);
    }

    public AuthUserResponse updateDisplayName(CognitoUserInfo cognitoUserInfo, String displayName, boolean isAdmin) {
        User user = findOrCreate(cognitoUserInfo);
        user.setDisplayName(displayName);
        return authUserMapper.toDTO(userRepository.save(user), displayName, isAdmin);
    }

    private User findOrCreate(CognitoUserInfo cognitoUserInfo) {
        String cognitoSub = cognitoUserInfo.sub();
        return this.userRepository.findByCognitoSub(cognitoSub)
                .orElseGet(() -> {
                    try {
                        return createAndSaveFromCognito(cognitoUserInfo);
                    } catch (DataIntegrityViolationException e) {
                        return this.userRepository.findByCognitoSub(cognitoSub)
                                .orElseThrow(() -> e);
                    }
                });
    }

    protected User createAndSaveFromCognito(CognitoUserInfo cognitoUserInfo) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(cognitoUserInfo.email());
        user.setDisplayName(cognitoUserInfo.preferredUsername());
        user.setCognitoSub(cognitoUserInfo.sub());
        this.userRepository.save(user);
        return user;
    }
}
