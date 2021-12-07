package com.moments.auth.model;


import com.mongodb.lang.Nullable;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class Profile {

    @NonNull
    private String fullName;
    private String profilePictureUrl;
}
