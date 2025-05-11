package com.ql.ecommerce.dto.user.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String role;
}
