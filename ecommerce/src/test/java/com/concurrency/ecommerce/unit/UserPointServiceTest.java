package com.concurrency.ecommerce.unit;

import com.concurrency.ecommerce.user.domain.UserPointService;
import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class UserPointServiceTest {

    @InjectMocks
    UserPointService sut;

    @Test
    @DisplayName("유저 id로 조회한 userId와 point를 UserPointDto 객체로 반환한다.")
    void getPoint() {
        //given
        Long userId = 1L;

        //when
        UserPointDto result = sut.getPoint(userId);

        //then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getPoint()).isNotNull();
    }

    @Test
    void charge() {
    }
}