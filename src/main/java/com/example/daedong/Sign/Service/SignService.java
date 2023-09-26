package com.example.daedong.Sign.Service;

import com.example.daedong.Dto.User;
import com.example.daedong.Dto.UserForm;

public interface SignService {
    // 로그인
    // 1. 회원 정보 및 비밀번호 조회
    // 2. 회원 정보 및 비밀번호 체크
    // 3. 회원 응답 객체에서 비밀번호를 제거한 후 회원 정보 리턴

    // 로그아웃

    // 회원 정보 저장 (회원가입)
    String saveMember(UserForm userForm);



    // 학교 이메일 중복검사
    boolean checkSchoolEmail(String schoolEmail);
}
