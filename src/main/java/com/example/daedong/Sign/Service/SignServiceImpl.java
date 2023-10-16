package com.example.daedong.Sign.Service;

import com.example.daedong.Dto.User;
import com.example.daedong.Dto.UserForm;
import com.example.daedong.Main.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignServiceImpl implements SignService {
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    // 로그인
    public User login(String schoolEmail, String password){
        // 1. 회원 정보 및 비밀번호 조회
        User user = userRepository.findBySchoolEmail(schoolEmail);
        if(user == null){
            return null;
        }
        String encodedPassword = (user == null) ? "" : user.getPassword();

        // 2. 회원 정보 및 비밀번호 체크
        if(user == null || passwordEncoder.matches(password, encodedPassword) == false){
            return null;
        }

        // 3. 회원 응답 객체에서 비밀번호를 제거한 후 회원 정보 리턴
        //member.clearPassword();
        return user;
    }

    // 회원가입
    @Override
    @Transactional
    public String saveMember(UserForm userForm) {
        User user = new User();
//        member.createMember(memberForm,passwordEncoder);
        List<String> chatRoomOid = new ArrayList<>();

        user.setName(userForm.getName());
        user.setSchoolEmail(userForm.getSchoolEmail());
        user.setSchoolName(userForm.getSchoolName());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(userForm.getPassword()));
        user.setPushAlarm(userForm.isPushAlarm());
        user.setPersonalInformation(userForm.isPersonalInformation());
        user.setChatRoomOid(chatRoomOid);
        userRepository.save(user);


        return "success";
    }

    //   schoolEmail(ID) 중복 확인
    @Override
    public boolean checkSchoolEmail(String schoolEmail) {
        User user = userRepository.findBySchoolEmail(schoolEmail);
        if(user != null){
            return false;
        }
        return true;
    }

    // 회원 정보 수정 user는 업데이트 된 user
    @Override
    @Transactional
    public User updateMember(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User updateUser = userRepository.save(user);
        if(updateUser != null){
            return updateUser;
        }else{
            return null;
        }
    }

    //회원 정보 삭제(회원 탈퇴)
    @Override
    public String deleteMember(User user) {
        try {
            userRepository.deleteById(user.get_id());
        }catch (Exception e){
            e.printStackTrace();
            return "false";
        }
        return "success";
    }
}
