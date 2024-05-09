package com.concurrency.ecommerce.user.infra;

import com.concurrency.ecommerce.user.domain.model.UserPointDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.math.BigInteger;

@Entity
@Table(name = "tb_user")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "BIGINT")
    private BigInteger point;

    @Version
    @Column(columnDefinition = "BIGINT DEFAULT 1")
    private Long version;

    public UserPointDto toDomain() {
        return UserPointDto.builder()
                .userId(id)
                .point(point)
                .version(version)
                .build();
    }

    public static User fromDomain(UserPointDto dto) {
        return User.builder()
                .id(dto.getUserId())
                .point(dto.getPoint())
                .version(dto.getVersion())
                .build();
    }
}
