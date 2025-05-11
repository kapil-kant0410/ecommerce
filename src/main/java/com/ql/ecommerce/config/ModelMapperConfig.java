package com.ql.ecommerce.config;

import com.ql.ecommerce.dto.user.response.UserDto;
import com.ql.ecommerce.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper() ;
    }

}
