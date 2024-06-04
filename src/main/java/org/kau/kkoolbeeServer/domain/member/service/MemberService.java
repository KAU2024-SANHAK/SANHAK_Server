package org.kau.kkoolbeeServer.domain.member.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kau.kkoolbeeServer.domain.member.Member;
import org.kau.kkoolbeeServer.domain.member.UserDiaryType;
import org.kau.kkoolbeeServer.domain.member.dto.response.MemberLoginResponseDto;
import org.kau.kkoolbeeServer.domain.member.repository.MemberRepository;
import org.kau.kkoolbeeServer.global.auth.fegin.kakao.KakaoLoginService;
import org.kau.kkoolbeeServer.global.auth.jwt.JwtProvider;
import org.kau.kkoolbeeServer.global.auth.jwt.TokenDto;
import org.kau.kkoolbeeServer.global.auth.security.UserAuthentication;
import org.kau.kkoolbeeServer.global.common.exception.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.kau.kkoolbeeServer.global.common.dto.enums.ErrorType.INVALID_TOKEN_HEADER_ERROR;
import static org.kau.kkoolbeeServer.global.common.dto.enums.ErrorType.NOT_FOUND_MEMBER_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    @Value("${discord.webhook.url}")
    private String discordWebhookUrl;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final KakaoLoginService kakaoLoginService;

    private static String parseTokenString(String tokenString) {
        String[] strings = tokenString.split(" ");
        if (strings.length != 2) {
            throw new CustomException(INVALID_TOKEN_HEADER_ERROR);
        }
        return strings[1];
    }

    @Transactional
    public MemberLoginResponseDto login(String socialAccessToken) {

        socialAccessToken = parseTokenString(socialAccessToken);

        String kakaoId = kakaoLoginService.getKakaoId(socialAccessToken);

        boolean isRegistered = isUserByKakaoId(kakaoId);
        Member loginMember;
        if (!isRegistered) {
            Member member = Member.builder()
                .kakaoId(kakaoId).build();

            memberRepository.save(member);
            loginMember = getUserBySocialAndSocialId(kakaoId);
            // 카카오 로그인은 정보 더 많이 받아올 수 있으므로 추가 설정
            kakaoLoginService.setKakaoInfo(loginMember, socialAccessToken);
            sendDiscordNotification(member.getSocialNickname());

        }
        else{
            loginMember = getUserBySocialAndSocialId(kakaoId);
            // 카카오 로그인은 정보 더 많이 받아올 수 있으므로 추가 설정
            kakaoLoginService.setKakaoInfo(loginMember, socialAccessToken);
        }



        TokenDto tokenDto = jwtProvider.issueToken(
            new UserAuthentication(loginMember.getId(), null, null));

        return MemberLoginResponseDto.of(loginMember, tokenDto);
    }

    @Transactional
    public TokenDto reissueToken(String refreshToken) {

        refreshToken = parseTokenString(refreshToken);

        Long memberId = jwtProvider.validateRefreshToken(refreshToken);
        validateMemberId(memberId);  // memberId가 DB에 저장된 유효한 값인지 검사

        jwtProvider.deleteRefreshToken(memberId);
        return jwtProvider.issueToken(new UserAuthentication(memberId, null, null));
    }

    @Transactional
    public void logout(Long memberId) {
        jwtProvider.deleteRefreshToken(memberId);
    }

    private void validateMemberId(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(NOT_FOUND_MEMBER_ERROR);
        }
    }

    private Member getUserBySocialAndSocialId(String kakaoId) {
        return memberRepository.findByKakaoId(kakaoId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));
    }

    private boolean isUserByKakaoId(String kakaoId) {
        return memberRepository.existsByKakaoId(kakaoId);
    }

    @Transactional
    public void setUserDiaryType(Long memberId, UserDiaryType userDiaryType){

        Member member=memberRepository.findByIdOrThrow(memberId);
        member.setDiaryType(userDiaryType);
        memberRepository.save(member);

    }

    @Transactional(readOnly = true)
    public Member findByIdOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_MEMBER_ERROR));
    }
    private void sendDiscordNotification(String nickname) {
        RestTemplate restTemplate = new RestTemplate();

        Long totalMembers = memberRepository.count();

        String message = totalMembers + "번째 멤버가 회원가입했습니다.\n" +
                "사용자명: " + nickname;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("content", message);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(discordWebhookUrl, requestEntity, String.class);


    }


  /*  @Transactional
    public void setUserDiaryType(String Token, String userDiaryType){

        Token=parseTokenString(Token);
        UserDiaryType DiaryType=UserDiaryType.valueOf(userDiaryType);

        jwtProvider.getUserFromJwt()




    }*/
}