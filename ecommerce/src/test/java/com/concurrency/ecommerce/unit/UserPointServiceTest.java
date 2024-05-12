package com.concurrency.ecommerce.unit;

import com.concurrency.ecommerce.common.EcommerceException;
import com.concurrency.ecommerce.common.ErrorCode;
import com.concurrency.ecommerce.user.domain.UserPointRepository;
import com.concurrency.ecommerce.user.domain.UserPointService;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class UserPointServiceTest {

    @InjectMocks
    UserPointService sut;

    @Mock
    UserPointRepository userPointRepository;

    /**
     * 포인트 조회 기능 테스트
     */
    @Test
    @DisplayName("유저 id로 포인트 조회 성공 시 userId와 point를 UserPointDto 객체로 반환한다.")
    void successGetUserPoint() {
        //given
        Long userId = 1L;
        BigInteger point = BigInteger.valueOf(10000);

        //when
        when(userPointRepository.getUserPoint(userId)).thenReturn(Optional.of(new UserPointDto(userId, point)));
        UserPointDto result = sut.getPoint(userId);

        //then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getPoint()).isEqualTo(point);
    }

    @Test
    @DisplayName("유저 id로 포인트 조회 실패 시 EcommerceException(ErrorCode.NOT_FOUND_USER)를 반환한다.")
    void NOT_FOUND_USER() {
        //given
        Long userId = 1L;
        BigInteger point = BigInteger.valueOf(10000);
        Throwable exception = null;

        //when
        when(userPointRepository.getUserPoint(userId)).thenReturn(Optional.empty());
        try {
            sut.getPoint(userId);
        } catch (EcommerceException e) {
            exception = e;
        }

        //then
        assert exception != null;
        assert exception instanceof EcommerceException;
        assert ((EcommerceException) exception).getErrorCode() == ErrorCode.NOT_FOUND_USER;
        assert ((EcommerceException) exception).getErrorCode().getMessage().equals("일치하는 유저가 없습니다.");
    }

    /**
     * 포인트 충전 기능 테스트
     */
    @Test
    @DisplayName("유저 id와 충전 포인트를 UserPointDto로 포인트 업데이트 성공 시 UserPointDto 객체로 반환한다.")
    void successSaveUserPoint() {
        //given
        Long userId = 1L;
        BigInteger originPoint = BigInteger.ZERO;
        BigInteger requestPoint = BigInteger.valueOf(30000);

        UserPointDto originUser = new UserPointDto(userId, originPoint);
        given(userPointRepository.getUserPoint(userId)).willReturn(Optional.of(originUser));

        UserPointDto testUser = new UserPointDto(userId, originUser.getPoint().add(requestPoint));
        given(userPointRepository.saveUserPoint(any())).willReturn(testUser);

        //when
        UserPointDto result = userPointRepository.saveUserPoint(testUser);

        //then
        assertThat(result.getPoint()).isEqualTo(originPoint.add(requestPoint));
    }
}