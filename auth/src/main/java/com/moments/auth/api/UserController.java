package com.moments.auth.api;


import com.moments.auth.exception.ResourceNotFoundException;
import com.moments.auth.model.Profile;
import com.moments.auth.model.User;
import com.moments.auth.payload.JwtAuthenticationResponse;
import com.moments.auth.payload.LoginRequest;
import com.moments.auth.payload.UserRegisterPayload;
import com.moments.auth.payload.UserSummary;
import com.moments.auth.service.InstaUserDetailsService;
import com.moments.auth.service.JwtTokenProvider;
import com.moments.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.basic.BasicDesktopIconUI;
import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.*;


@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired // TODO can we use the parent class ? UserDetailsService
    private InstaUserDetailsService instaUserDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/hello")
    ResponseEntity<String> hello() {

        return new ResponseEntity<>("Hello World! karun", HttpStatus.OK);
    }


    @PostMapping(value = "/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)  {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // we do we need this?
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }



    //    https://stackoverflow.com/questions/45635302/whats-the-reason-to-use-
    //    responseentity-return-type-instead-of-simple-respons?noredirect=1&lq=1
    //    https://stackoverflow.com/questions/3595160/what-does-the-valid-annotation-indicate-in-spring

    //TODO api response is not good when content type is other than json.
    @PostMapping(value ="/register",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
        public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterPayload userRegisterPayload){

        User user = User.builder()
                        .username(userRegisterPayload.getUsername())
                        .email(userRegisterPayload.getEmail())
                        .password(userRegisterPayload.getPassword())
                        .profile(
                            Profile.builder().
                            fullName(userRegisterPayload.getName())
                            .build()
                        ).build();

        System.out.println(user);
        // TODO need to hanndle save the profile details tooo
        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value="/users/{username}",produces= APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUser(@PathVariable("username") String username){
        return userService
                .findByUsername(username)
                .map(user->ResponseEntity.ok(user))
                .orElseThrow(()->new ResourceNotFoundException(username));
    }

    @GetMapping(value = "/users",produces= APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll(){

        return ResponseEntity.ok(userService.findAll());
    }


    @GetMapping(value = "/users/{username}/summary",produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserSummary(@PathVariable("username") String userName){
        return userService
                .findByUsername(userName)
                .map(user -> ResponseEntity.ok(convertToUserDetail(user)))
                .orElseThrow(() -> new ResourceNotFoundException(userName));
    }

    @PostMapping(value = "/users/summarylist",produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserSummaries(@RequestBody List<String> userNames){
        List<UserSummary> userSummaryList =
                userService
                        .findByUsernameIn(userNames)
                        .stream()
                        .map(user -> convertToUserDetail(user))
                        .collect(Collectors.toList());
        return  ResponseEntity.ok(userSummaryList);
    }




    private UserSummary convertToUserDetail(User user){
        return UserSummary
                .builder()
                .fullName(user.getProfile().getFullName())
                .profilePicture(user.getProfile().getProfilePictureUrl())
                .userName(user.getUsername())
                .id(user.getId())
                .build();
    }




}
