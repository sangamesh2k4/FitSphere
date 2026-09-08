package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.entity.User;
import com.sangamesh.Fitsphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetails loadUserByUsername(String userId){
        User user=userRepository.findById(Long.parseLong(userId))
                .orElseThrow(()-> new UsernameNotFoundException("user not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername()).password(user.getPassword()).authorities(
                new SimpleGrantedAuthority(user.getRole().name())).disabled(!user.getEnabled()).build();

    }

}
