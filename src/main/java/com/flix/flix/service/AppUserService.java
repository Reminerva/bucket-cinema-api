package com.flix.flix.service;

import com.flix.flix.entity.AppUser;
import com.flix.flix.model.request.LoginRequest;
import com.flix.flix.model.request.NewUserRequest;
import com.flix.flix.model.response.SigninResponse;
import com.flix.flix.model.response.SignoutResponse;
import com.flix.flix.model.response.SignupResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AppUserService {
    AppUser getAppUserById (String id);
    SignupResponse signup (NewUserRequest userRequest);
    SigninResponse signin (LoginRequest userRequest);
    SignoutResponse signout (HttpServletRequest signoutRequest);

}