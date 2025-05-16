package com.flix.flix.model.response;

import java.util.List;

import com.flix.flix.constant.custom_enum.ERole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUserResponse {
    private String id;
    private String username;
    private String email;
    private List<ERole> role;
    private String customerFullname;
}
