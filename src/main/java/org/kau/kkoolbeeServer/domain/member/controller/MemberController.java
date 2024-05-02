package org.kau.kkoolbeeServer.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.kau.kkoolbeeServer.domain.member.UserDiaryType;
import org.kau.kkoolbeeServer.domain.member.dto.response.MemberLoginResponseDto;
import org.kau.kkoolbeeServer.domain.member.service.MemberService;
import org.kau.kkoolbeeServer.global.auth.fegin.kakao.KakaoLoginService;
import org.kau.kkoolbeeServer.global.auth.jwt.JwtProvider;
import org.kau.kkoolbeeServer.global.auth.jwt.TokenDto;
import org.kau.kkoolbeeServer.global.common.dto.ApiResponse;
import org.kau.kkoolbeeServer.global.common.dto.enums.SuccessType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final KakaoLoginService kakaoLoginService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberLoginResponseDto>> login(
        @RequestHeader("Authorization") String socialAccessToken) {

        return ResponseEntity.ok(ApiResponse.success(SuccessType.LOGIN_SUCCESS, memberService.login(socialAccessToken)));
    }

    @GetMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenDto>> reissue(
        @RequestHeader("Authorization") String refreshToken) {

        return ResponseEntity.ok(ApiResponse.success(SuccessType.REISSUE_SUCCESS, memberService.reissueToken(refreshToken)));
    }

    @PatchMapping("/log-out") // Spring Security 자체의 logout과 겹치지 않기 위해 이렇게 설정
    public ResponseEntity<ApiResponse<?>> logout(Principal principal) {

        memberService.logout(JwtProvider.getUserFromPrincipal(principal));
        return ResponseEntity.ok(ApiResponse.success(SuccessType.LOGOUT_SUCCESS));
    }

    @GetMapping("/kakao")
    public ResponseEntity<ApiResponse<?>> kakaoAccessToken(
        @RequestHeader("Authorization") String code) {
        return ResponseEntity.ok(ApiResponse.success(SuccessType.KAKAO_ACCESS_TOKEN_SUCCESS, kakaoLoginService.getKakaoAccessToken(code)));
    }

    @PostMapping("/member/character")
    public ResponseEntity<ApiResponse<?>> diaryType(Principal principal,@RequestBody String userDiaryType){

        Long memberId=JwtProvider.getUserFromPrincipal(principal);

        UserDiaryType diaryType=UserDiaryType.valueOf(userDiaryType);
        memberService.setUserDiaryType(memberId,diaryType);

        return ResponseEntity.ok(ApiResponse.success(SuccessType.PROCESS_SUCCESSED));


    }


}