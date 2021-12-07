package com.moments.auth.payload;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSummary {
    private String id;
    private String userName;
    private String fullName;
    private String profilePicture;
}
