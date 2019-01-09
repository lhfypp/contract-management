package com.snxy.contract.service.vo;

import lombok.*;

import java.util.List;

/**
 * Created by lvhai on 2019/1/8.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AppContractVo {
    String category;
    int categoryOrder;
    private List<FieldValue> fieldValues;

    @Override
    public boolean equals(Object anObject) {
        if (anObject == null)
            return false;
        if (!(anObject instanceof AppContractVo))
            return false;
        //忽略 categoryOrder
        return category.equals(((AppContractVo) anObject).category);
    }

    @Override
    public int hashCode() {
        return category.hashCode();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class FieldValue  {
        private String code;
        private String name;
        private int fieldOrder;
        private String value;

    }
}
