package com.ocr.paymybuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCustom implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Override
    public String toString() {
        return "UserCustom{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )

    List<FriendShip> friendShipList = new ArrayList<>();

    @OneToOne(mappedBy = "userCustom")
    private BankAccount bankAccount;


    /**
     * Add a new relationship
     * @param friend friend
     */
    public void addFriend(UserCustom friend) {

        Optional<FriendShip> existingFriendShip = friendShipList.stream()
                .filter(friendShip -> friendShip.getFriend().equals(friend))
                .findFirst();
        if (existingFriendShip.isPresent()) {
            existingFriendShip.get().setFriend(friend);
        } else {
            FriendShip friendShip = new FriendShip();
            friendShip.setUser(this);
            friendShip.setFriend(friend);
            friendShipList.add(friendShip);
        }
    }

    /**
     * Delete a relationship
     * @param friend friend
     */
    public void deleteFriend(UserCustom friend) {
        this.friendShipList.stream()
                .filter(friendShip -> friendShip.getFriend().equals(friend))
                .findFirst().ifPresent(friendShipToRemove -> this.friendShipList.remove(friendShipToRemove));

    }

    /**
     * Get full name for the front
     * @return string
     */
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
