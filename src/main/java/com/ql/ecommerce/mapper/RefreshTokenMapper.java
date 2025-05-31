package com.ql.ecommerce.mapper;

import com.ql.ecommerce.entity.RefreshToken;
import com.ql.ecommerce.entity.User;
import com.ql.ecommerce.repository.RefreshTokenRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RefreshTokenMapper {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenMapper (RefreshTokenRepository refreshTokenRepository){
        this.refreshTokenRepository=refreshTokenRepository;
    }

    public RefreshToken toEntity(User user, String refreshToken){
        RefreshToken refreshTokenEntity=new RefreshToken();
        refreshTokenEntity.setRefToken(refreshToken);
        refreshTokenEntity.setUpdatedAt(LocalDateTime.now());
        refreshTokenEntity.setUser(user);
        return refreshTokenEntity;
    }

   public void updateRefreshToken(RefreshToken oldRefreshToken,String newRefreshToken){
        oldRefreshToken.setRefToken(newRefreshToken);
        oldRefreshToken.setUpdatedAt(LocalDateTime.now());
        refreshTokenRepository.save(oldRefreshToken);
    }

}
