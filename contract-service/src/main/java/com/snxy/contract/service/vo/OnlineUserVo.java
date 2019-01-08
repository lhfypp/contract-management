package com.snxy.contract.service.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlineUserVo {
    private Long id;

    private String userName;

    private String phone;

}