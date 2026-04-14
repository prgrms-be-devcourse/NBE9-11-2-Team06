package com.back.nbe9112team06.domain.user.entity;

import com.back.nbe9112team06.domain.meeting.entity.Meeting;
import com.back.nbe9112team06.global.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    private String nickname;

    private String timezone;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
            , orphanRemoval = true)
    private List<Meeting> meetings = new ArrayList<>();

    public User(String email, String passwordHash, String nickname, String timezone) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.timezone = timezone;
    }
}
