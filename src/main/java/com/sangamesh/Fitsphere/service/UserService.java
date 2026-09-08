package com.sangamesh.Fitsphere.service;

import com.sangamesh.Fitsphere.entity.User;

public interface UserService {

    User getCurrentUser();
    User findById(Long id);
}
