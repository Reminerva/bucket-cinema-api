package com.flix.flix.model.response;

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
public class EmployeeResponse {

    private String id;
    private String fullname;
    private String nikNumber;
    private String address;
    private String phoneNumber;
    private String gender;
    private String city;
    private String dateOfBirth;
    private String dateOfAppliment;
    private String appUserUsername;
    private String appUserEmail;
    private String theaterId;
    private List<String> transactionsId;
    private Boolean isActive;

}
