package com.project.service;

import com.project.config.security.JwtTokenProvider;
import com.project.domain.Center;
import com.project.domain.Equipment;
import com.project.domain.Trainer;
import com.project.domain.User;
import com.project.exception.EmailSigninFailed;
import com.project.exception.UserNotFound;
import com.project.repository.CenterRepository;
import com.project.repository.TrainerRepository;
import com.project.repository.UserRepository;
import com.project.request.*;
import com.project.response.CenterSignResponse;
import com.project.response.KakaoAuth;
import com.project.response.KakaoProfile;
import com.project.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class SignService  {
//public class SignService implements UserDetailsService {

    private final UserRepository userRepository;

    private final CenterRepository centerRepository;

    private final PasswordEncoder passwordEncoder;


    private final TrainerRepository trainerRepository;

    private static final String BEARER_TYPE = "Bearer";
    private final RedisTemplate redisTemplate;

    private final JwtTokenProvider jwtTokenProvider;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;              // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;    // 7일



    //    public UserDetails loadUserByUsername(String userPk) {
//        return userRepository.findById(Long.valueOf(userPk)).orElseThrow(UserNotFoundException::new);
//    }
    // 현재는 여기서 loadUserByUsername를 구현하지 않음
    public void join(UserSignUp request){
        User user= User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .name(request.getName())
                .birth(request.getBirth())
                .gender(request.getGender())
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
        userRepository.save(user);
    }
    @Transactional
    public CenterSignResponse joinCenter(CenterSignUp request){
        Center center= Center.builder()
                .name(request.getName())
                .region(request.getRegion())
                .address(request.getAddress())
                .price(request.getPrice())
                .phoneNumber(request.getPhoneNumber())
                .storeNumber(request.getStoreNumber())
                .build();

        Center newCenter = centerRepository.saveAndFlush(center);
        User user= User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .name(request.getName())
                .center(center)
                .roles(Collections.singletonList("ROLE_CENTER"))
                .build();
        User newUser = userRepository.saveAndFlush(user);
        return CenterSignResponse
                .builder().centerId(newCenter.getId())
                .userId(newUser.getMsrl())
                .build();

    }

    public Optional<User> getByEmail(String request){
        return userRepository.findByEmail(request);
    }
    public Optional<User> getByPhoneNumber(String request){
        return userRepository.findByPhoneNumber(request);
    }

    public TokenResponse signIn(UserSignIn request){
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(EmailSigninFailed::new);
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new EmailSigninFailed();
        }
        String accessToken = jwtTokenProvider.createToken(String.valueOf(user.getMsrl()), user.getRoles(), ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = jwtTokenProvider.createToken(String.valueOf(user.getMsrl()), user.getRoles(), REFRESH_TOKEN_EXPIRE_TIME);
        redisTemplate.opsForValue()
                .set("RT:" + user.getEmail(), refreshToken , REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        return TokenResponse
                .builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }


    public TokenResponse signInByKakao(long kakaoId){
        User user = userRepository.findByKakao(kakaoId).orElseThrow(UserNotFound::new);
        String accessToken = jwtTokenProvider.createToken(String.valueOf(user.getMsrl()), user.getRoles(), ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = jwtTokenProvider.createToken(String.valueOf(user.getMsrl()), user.getRoles(), REFRESH_TOKEN_EXPIRE_TIME);
        return TokenResponse
                .builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    //회원가입 이후 카카오 ID 설정
    public void joinByKakao(KakaoSignUp kakaoSignUp){
        User user = userRepository.findByEmail(kakaoSignUp.getEmail()).orElseThrow(UserNotFound::new);
        user.setKakao(kakaoSignUp.getKakaoId());
        userRepository.save(user);
    }


    @Transactional
    public TokenResponse reissue(TokenRequest tokenRequest) { //추후 리퀘스트로 수정
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(tokenRequest.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }
        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequest.getRefreshToken());
        // 3. Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옵니다.
        String refreshToken = (String)redisTemplate.opsForValue().get("RT:" + authentication.getName());
        if(!refreshToken.equals(tokenRequest.getRefreshToken())) {
            return null;
        }
        User principal = (User) authentication.getPrincipal();
        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.equals(tokenRequest.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }
//         5. 새로운 Access 토큰 생성
        String newAccessToken = jwtTokenProvider.createToken(jwtTokenProvider.getUserPk(refreshToken),principal.getRoles(),ACCESS_TOKEN_EXPIRE_TIME);
//         토큰 발급
        return TokenResponse.builder().accessToken(newAccessToken).refreshToken(tokenRequest.getRefreshToken()).build();
    }

    public String makeNewPassword(String request){
        User user= userRepository.findByEmail(request).orElseThrow(UserNotFound::new);
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        user.setPassword(passwordEncoder.encode(generatedString));
        userRepository.save(user);
        return generatedString;
    }

    public void addTrainer(Center center, TrainerRequest request){
        Trainer trainer  = Trainer.builder()
                .center(center)
                .name(request.getName())
                .gender(request.getGender())
                .build();
        trainerRepository.save(trainer);
    }
}

