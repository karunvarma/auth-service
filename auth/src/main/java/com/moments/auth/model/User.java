package com.moments.auth.model;


import jdk.jfr.DataAmount;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Set;


//TODO need to check the working of createdAt and updated values

// collection name users
@Document("Users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {


    public User(User user) {
        this.id = user.id;
        this.username = user.username;
        this.password = user.password;
        this.email = user.email;
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.isActive = true;
        this.profile = user.profile;
        this.roles = user.roles;
    }

    @Id
    private String id;

    @NotBlank
    @Size(max = 20,min = 5)
    private String username;

    @NotBlank
    @Size(min = 7, max = 30)
    private  String password;

    @NotBlank
    @Email()
    private String email;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private boolean isActive;

    private Profile profile;

    private Set<Role> roles;
}
