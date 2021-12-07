package com.moments.auth.payload;

import com.moments.auth.messaging.MessageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEventPayload {
    private String id;
    private String username;
    private String email;
    private String displayName;
    private String profilePictureUrl;
//    private String oldProfilePicUrl;
    private MessageType eventType;
}
