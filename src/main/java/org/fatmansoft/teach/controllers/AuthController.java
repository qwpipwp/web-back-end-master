package org.fatmansoft.teach.controllers;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.fatmansoft.teach.models.EUserType;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.User;
import org.fatmansoft.teach.models.UserType;
import org.fatmansoft.teach.payload.request.*;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.repository.UserTypeRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.fatmansoft.teach.payload.response.JwtResponse;
import org.fatmansoft.teach.payload.response.MessageResponse;
import org.fatmansoft.teach.repository.UserRepository;
import org.fatmansoft.teach.security.jwt.JwtUtils;
import org.fatmansoft.teach.security.services.UserDetailsImpl;
import org.yaml.snakeyaml.Yaml;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserTypeRepository userTypeRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private ResourceLoader resourceLoader;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles.get(0)));
    }



    @PostMapping("/signup")
    public String registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("Error: Username is already taken!"));
            return "用户名被占用";
        }

        if(personRepository.existsByEmail(signUpRequest.getEmail())){
//            return ResponseEntity
//                    .badRequest()
//                    .body(new MessageResponse("邮箱被占用了"));
            return "邮箱被占用";
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
//        System.out.println(strRoles);
        if (strRoles == null) {
            UserType userRole = userTypeRepository.findByName(EUserType.ROLE_USER);
            user.setUserType(userRole);
        } else {
            strRoles.forEach(role -> {
//                System.out.println(role);
                switch (role) {
                    case "管理员":
                        UserType adminRole = userTypeRepository.findByName(EUserType.ROLE_ADMIN);
                        user.setUserType(adminRole);
                        break;
                    case "老师":
                        UserType teacherRole = userTypeRepository.findByName(EUserType.ROLE_TEACHER);
                        user.setUserType(teacherRole);
                        break;
                    case "学生":
                        UserType studentRole = userTypeRepository.findByName(EUserType.ROLE_STUDENT);
                        user.setUserType(studentRole);
                        break;

                    default:
                        UserType userRole = userTypeRepository.findByName(EUserType.ROLE_USER);
                        user.setUserType(userRole);
                }
            });
        }
        //person类的实例化和储存
        Person person = new Person(personRepository.getNextId(),signUpRequest.getUsername(),signUpRequest.getEmail());
        String email = signUpRequest.getEmail();
//        System.out.println(email);
        personRepository.save(person);
        //设置userid、person属性，储存user
        user.setUserId(userRepository.getNextId());
        user.setPerson(person);
        userRepository.save(user);

        return "成功注册";
    }
    @PostMapping("/getUimsConfig")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public DataResponse getUimsConfig(@Valid @RequestBody DataRequest dataRequest) {
        Map data = new HashMap();;
        InputStream in = null;
        try {
            Yaml yaml = new Yaml();
            Resource resource = resourceLoader.getResource("classpath:uims.yml");
            in = resource.getInputStream();
            data =(Map)yaml.load(in);
        }catch(Exception e){

        }
        return CommonMethod.getReturnData(data);
    }
    @PostMapping("/identifyRole")
    public String identyfyRole(@Valid @RequestBody IdentifyRoleRequest identifyRoleRequest){
        String username = identifyRoleRequest.getUsername();
        System.out.println(username);
        Integer userTypeId = 0;
        if(userRepository.getRole(username) != null)
            userTypeId = userRepository.getRole(username);
        System.out.println(userTypeId);
        if(userTypeId == 2)
            return "学生";
        if(userTypeId == 3)
            return "老师";
        if(userTypeId ==1)
            return "管理员";
        return "";
    }

    @PostMapping("/changePassword")
    public String changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest){
        String username = changePasswordRequest.getUsername();
        String newPassword = changePasswordRequest.getPassword();
        Integer id = userRepository.getIdByUserName(username);
        Optional<User> obj = userRepository.findByUserId(id);
        User user = obj.get();
        user.setPassword(encoder.encode((newPassword)));
        userRepository.save(user);
        return "修改完毕";
    }


    @PostMapping("/findBackPassword")
    public String findBackPassword(@Valid @RequestBody FindBackRequest findBackRequest){
        Integer personId = personRepository.getIdByEmail(findBackRequest.getEmail());
        String username = userRepository.getUsernameByPersonId(personId);
        String newPassword = findBackRequest.getPassword();
        Integer id = userRepository.getIdByUserName(username);
        Optional<User> obj = userRepository.findByUserId(id);
        User user = obj.get();
        user.setPassword(encoder.encode((newPassword)));
        userRepository.save(user);
        return "修改完毕";
    }


}
