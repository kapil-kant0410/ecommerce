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
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenEntity.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenEntity.setUser(user);
        return refreshTokenEntity;
    }

   public void updateRefreshToken(RefreshToken oldRefreshToken,String newRefreshToken){
        oldRefreshToken.setRefToken(newRefreshToken);
        oldRefreshToken.setCreatedAt(LocalDateTime.now());
        oldRefreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(oldRefreshToken);
    }

}
