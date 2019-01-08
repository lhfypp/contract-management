package com.snxy.contract.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlineUser {
    private Long id;

    private Long systemUserId;

    private String userName;

    private String phone;

    private Byte isDelete;


}