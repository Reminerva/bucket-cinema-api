package com.flix.flix.model.request.search;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchAppUserRequest {

    private String username;
    private String email;
    private List<String> role;
    private String customerFullname;

    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
}
