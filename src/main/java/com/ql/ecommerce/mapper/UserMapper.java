package com.ql.ecommerce.mapper;

import com.ql.ecommerce.dto.auth.request.EmailPasswordRegisterRequest;
import com.ql.ecommerce.dto.user.response.UserDto;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.enums.Role;
import com.ql.ecommerce.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class UserMapper {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserMapper(UserRepository userRepository,ModelMapper modelMapper,PasswordEncoder passwordEncoder) {
        this.modelMapper=modelMapper;
        this.passwordEncoder=passwordEncoder;
        this.userRepository=userRepository;
    }

    public UserDto toDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public List<UserDto> toDtoList(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .toList();
    }

    public User toEntity(EmailPasswordRegisterRequest emailPasswordRegisterRequest){
        User user = new User();
        user.setName(emailPasswordRegisterRequest.getName());
        user.setEmail(emailPasswordRegisterRequest.getEmail());
        user.setPassword(passwordEncoder.encode(emailPasswordRegisterRequest.getPassword()));
        user.setRole(Role.valueOf(emailPasswordRegisterRequest.getRole()));
        return user;
    }

    public void updatePassword(User user,String newPassword){
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
