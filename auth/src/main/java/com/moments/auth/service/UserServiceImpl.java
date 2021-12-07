package com.moments.auth.service;

import com.moments.auth.exception.EmailAlreadyExistsException;
import com.moments.auth.exception.UsernameAlreadyExistsException;
import com.moments.auth.messaging.MessageType;
import com.moments.auth.model.Role;
import com.moments.auth.model.User;
import com.moments.auth.payload.UserEventPayload;
import com.moments.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

//https://projectlombok.org/features/log
@Service
@Slf4j
public class UserServiceImpl implements UserService{

    private static final String PRODUCER_BINDING_NAME = "userProducer-out-0";

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private StreamBridge streamBridge;


    @Override
    public User registerUser(User user) {
        log.info("About to register  user {}",user.getUsername());//https://stackoverflow.com/a/2031209 - log levels

        if(userRepository.existsByUsername(user.getUsername())){
            log.warn("email {} already exists", user.getUsername());

            throw new UsernameAlreadyExistsException(String.format("username %s is taken", user.getUsername()));
        }

        if(userRepository.existsByEmail(user.getEmail())){
            log.warn("email {} already exists",user.getEmail());

            throw new EmailAlreadyExistsException(String.format("email %s already exists",user.getEmail()));
        }
        user.setActive(true);
        //TODO there's an issue with importing encoders for encoding password, so leaving the password in plain text for now
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(){{
            add(Role.USER);
        }});

        User savedUser = userRepository.save(user);
        //TODO Set kafka event later

        UserEventPayload userEventPayload = convertTo(user,MessageType.CREATED);

        // invoke the producer binder to send the dynamic data
        // streamBridge.send(PRODUCER_BINDING_NAME, userEventPayload);

        return savedUser;
    }

    public Optional<User> findByUsername(String username){
        return userRepository
                .findByUsername(username);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public List<User> findByUsernameIn(List<String> userNames) {
        return userRepository.findByUsernameIn(userNames);
    }

    private UserEventPayload convertTo(User user, MessageType eventType) {
        return UserEventPayload
                .builder()
                .eventType(eventType)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getProfile().getFullName())
                .profilePictureUrl(user.getProfile().getProfilePictureUrl()).build();
    }

}
