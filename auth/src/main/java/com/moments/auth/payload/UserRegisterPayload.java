package com.moments.auth.payload;


import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

//https://www.baeldung.com/javax-validation
// TODO need to format api response for the invalid objects

@AllArgsConstructor
@Getter
public class UserRegisterPayload {

    @NotBlank
    @Size(min = 5, max = 20,message = "")
    private final String name;

    @NotBlank
    @Size(min = 5, max = 20)
    private final String username;

    @NotBlank
    @Size(min = 7, max = 30)
    private final String password;

    @NotBlank
    @Email(message = "Email should be valid")
    private final String email;

}
