package com.snxy.contract.business.utils;


import com.snxy.common.exception.BizException;
import com.snxy.contract.domain.ContractMetaData;
import com.snxy.contract.service.vo.JsContractMetaData;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lvhai on 2018/12/29.
 */
public class ContractTemplateEngine {
    /**
     * 编辑时替换
     */

    public final static String TEXTBOX_ID_PRE = "txt_";
    public final static String SPAN_ID_PRE = "span_";
    /**
     * 懒匹配
     */
    private final static String REGEX = "\\{\\$.*?\\$\\}";//"\\{\\$\\w+?\\$\\}";
    /**
     * 编辑时候用textbox <br>
     * id,class,width,value
     */
    private final static String TEXTBOX = "<input type=\"text\" id=\"%s\"  class=\"%s\" style=\"width:%dpx;\" value=\"%s\"></input>";
    /**
     * 包含RADIO的spanwidth（含宽度）<br>
     * class，width
     */
    private final static String WRAP_RADIO_START_WITH_WIDTH = "<span  class=\"%s\"  style=\"width:%dpx;display:inline-block;display:-moz-inline-box;text-indent: 0px;\">";
    private final static String WRAP_RADIO_START = "<span  class=\"%s\"  style=\"display:inline-block;display:-moz-inline-box;text-indent: 0px;\">";
    /**
     * radio控件 <br>
     * name,id,value,checked,disabled,text
     */
    private final static String RADIO = "<input type=\"radio\" name=\"%s\" id=\"%s\" value=\"%s\" %s %s />%s";
    private final static String WRAP_RADIO_END = "</span>";
    private final static int DEFAULT_WIDTH = 60;
    private final static String TEXTBOX_DEFAULT_STYLE = "cc";
    private final static String WRAP_RADIO_DEFAULT_STYLE = "";
    private final static String SPAN_DEFAULT_STYLE = "";
    /**
     * 浏览时候用span
     * id,class,width,value
     */
    private final static String SPAN = "<span   id=\"%s\"  class=\"%s\" style=\"width:%dpx;display:inline-block;display:-moz-inline-box;border-bottom:1px solid black;text-indent: 0px;\">%s</span>";

    private final static Pattern pattern = Pattern.compile(REGEX);

    /**
     * 校验模版的有效性，在模版保存前调用，避免编辑模版错误<br>
     * @param template
     * @param contractMetaDatas
     * @return 校验结果，如果是空字符串，则成功，否则，提供详细的错误提示，以供修正
     */
    public static String checkTemplateValid(String template, List<ContractMetaData> contractMetaDatas){
        throw new RuntimeException();
    }

    /**
     * @param template
     * @param contractMetaDatas
     * @return
     */
    public static String parseEditeView(String template, List<ContractMetaData> contractMetaDatas) {
        //注意:字段必须不相同，否则此处替换时候不好处理
        return parseView(template, contractMetaDatas, null, true);
    }

    public static String parseEditeView(String template, List<ContractMetaData> contractMetaDatas, Map<String, Object> valueMap) {
        //注意:字段必须不相同，否则此处替换时候不好处理
        return parseView(template, contractMetaDatas, valueMap, true);
    }

    public static String parseReadView(String template, List<ContractMetaData> contractMetaDatas, Map<String, Object> valueMap) {
        return parseView(template, contractMetaDatas, valueMap, false);
    }

    private static String parseView(String template, List<ContractMetaData> contractMetaDatas, Map<String, Object> valueMap, boolean isEdit) {
        //注意:字段必须不相同，否则此处替换时候不好处理
        if (StringUtils.isEmpty(template)) {
            return template;
        }
        StringBuilder replaceTemplate = new StringBuilder();
        Matcher matcher = pattern.matcher(template);
        int count = 0;
        int preEndIndex = 0;
        while (matcher.find()) {
            count++;
            int matchStart = matcher.start();
            int matchEnd = matcher.end();
            String replace;
            if (isEdit == true) { //editView
                replace = getEditeControl(matcher.group(), contractMetaDatas, valueMap);
            } else {//readView
                replace = getReadControl(matcher.group(), contractMetaDatas, valueMap);
            }

            replaceTemplate.append(template.substring(preEndIndex, matchStart));
            replaceTemplate.append(replace);

            preEndIndex = matchEnd;
        }
        if (preEndIndex < template.length()) {
            replaceTemplate.append(template.substring(preEndIndex));
        }
        return replaceTemplate.toString();
    }

    //region 获取编辑控件
    private static String getEditeControl(String rawCode, List<ContractMetaData> contractMetaDatas, Map<String, Object> valueMap) {
        //去掉开始的｛$和结尾的 $｝
        String code = rawCode.substring(2, rawCode.length() - 2);
        ContractMetaData contractMetaData = contractMetaDatas.stream().filter(cmd -> cmd.getCode().equals(code)).findFirst().orElse(null);
        if (contractMetaData == null) {
            throw new BizException(String.format("code[%s]没有找到元数据定义", code));
        }
        String defaultValue = "";
        if (StringUtils.isNotBlank(contractMetaData.getDefaultValue())) {
            defaultValue = contractMetaData.getDefaultValue();
        }
        if (valueMap != null) {
            Object dbValue = valueMap.get(contractMetaData.getCode());
            if (dbValue != null) {
                defaultValue = dbValue.toString();
            }
        }
        if (contractMetaData.getControlType() == 2) {
            return getRadioList(contractMetaData, defaultValue);
        }


        return getTextBox(contractMetaData, defaultValue);
    }

    private static String getTextBox(ContractMetaData contractMetaData, String defaultValue) {
        int width = DEFAULT_WIDTH;
        int defineWidth = contractMetaData.getWidth() == null ? 0 : contractMetaData.getWidth();
        if (defineWidth > 0) {
            width = defineWidth;
        }
        String textBoxCss = !StringUtils.isEmpty(contractMetaData.getCssName()) ? contractMetaData.getCssName() : TEXTBOX_DEFAULT_STYLE;

        return String.format(TEXTBOX, TEXTBOX_ID_PRE + contractMetaData.getCode(), textBoxCss, width, defaultValue);
    }

    private static String getRadioList(ContractMetaData contractMetaData, String defaultValue) {
        String code = contractMetaData.getCode();
        int defineWidth = contractMetaData.getWidth() == null ? 0 : contractMetaData.getWidth();
        if (StringUtils.isEmpty(contractMetaData.getDict())) {
            throw new BizException("code[%s]没有找到字典", code);
        }
        StringBuilder sbControl = new StringBuilder();
        String wrapRadioCss = !StringUtils.isEmpty(contractMetaData.getCssName()) ? contractMetaData.getCssName() : WRAP_RADIO_DEFAULT_STYLE;

        if (defineWidth > 0) {
            sbControl.append(String.format(WRAP_RADIO_START_WITH_WIDTH, wrapRadioCss, defineWidth));
        } else {
            sbControl.append(String.format(WRAP_RADIO_START, wrapRadioCss));
        }
        String[] vts = contractMetaData.getDict().split(";");

        for (String vt : vts) {
            String[] valueText = vt.split(",");
            String checked = "";

            if (valueText[0].equals(defaultValue)) {
                checked = "checked";
            }
            sbControl.append(String.format(RADIO, code, TEXTBOX_ID_PRE + code + valueText[0], valueText[0], checked, "", valueText[1]));
        }
        sbControl.append(WRAP_RADIO_END);
        return sbControl.toString();
    }
    //endregion

    //region 获取浏览控件
    private static String getReadControl(String rawCode, List<ContractMetaData> contractMetaDatas, Map<String, Object> valueMap) {
        //去掉开始的｛$和结尾的 $｝
        String code = rawCode.substring(2, rawCode.length() - 2);
        ContractMetaData contractMetaData = contractMetaDatas.stream().filter(cmd -> cmd.getCode().equals(code)).findFirst().orElse(null);
        if (contractMetaData == null) {
            throw new BizException(String.format("code[%s]没有找到元数据定义", code));
        }
        Object objValue = null;
//        if (valueMap.containsKey(code)) {//
        ///TODO 如果map不包含某个key，会不会报异常？
        objValue = valueMap.get(code);
//        }
        String value = objValue == null ? "" : objValue.toString();
        if (contractMetaData.getControlType() == 2) {
            return getReadRadioList(contractMetaData, value);
        }
        return getReadSpan(contractMetaData, value);
    }

    private static String getReadSpan(ContractMetaData contractMetaData, String value) {
        int width = DEFAULT_WIDTH;
        int defineWidth = contractMetaData.getWidth() == null ? 0 : contractMetaData.getWidth();
        if (defineWidth > 0) {
            width = defineWidth;
        }
        String code = contractMetaData.getCode();
        String textBoxCss = !StringUtils.isEmpty(contractMetaData.getCssName()) ? contractMetaData.getCssName() : TEXTBOX_DEFAULT_STYLE;
        return String.format(SPAN, SPAN_ID_PRE + code, textBoxCss, width, value);
    }

    private static String getReadRadioList(ContractMetaData contractMetaData, String value) {

        int defineWidth = contractMetaData.getWidth() == null ? 0 : contractMetaData.getWidth();
        String code = contractMetaData.getCode();
        if (StringUtils.isEmpty(contractMetaData.getDict())) {
            throw new BizException("code[%s]没有找到字典", code);
        }
        StringBuilder sbControl = new StringBuilder();
        String wrapRadioCss = !StringUtils.isEmpty(contractMetaData.getCssName()) ? contractMetaData.getCssName() : WRAP_RADIO_DEFAULT_STYLE;
        if (defineWidth > 0) {
            sbControl.append(String.format(WRAP_RADIO_START_WITH_WIDTH, wrapRadioCss, defineWidth));
        } else {
            sbControl.append(String.format(WRAP_RADIO_START, wrapRadioCss));
        }
        String[] vts = contractMetaData.getDict().split(";");
        for (String vt : vts) {
            String[] valueText = vt.split(",");
            String checked = "";
            if (valueText[0].equals(value)) {
                checked = "checked";
            }
            sbControl.append(String.format(RADIO, code, TEXTBOX_ID_PRE + code + valueText[0], valueText[0], checked, "disabled", valueText[1]));
        }
        sbControl.append(WRAP_RADIO_END);
        return sbControl.toString();
    }
    //endregion

    public static List<JsContractMetaData> getJsContractMetaData(List<ContractMetaData> contractMetaDatas) {
        List<JsContractMetaData> jsContractMetaDatas = new ArrayList<>(contractMetaDatas.size());
        contractMetaDatas.parallelStream().forEach(cmd -> {
            JsContractMetaData jcmd = new JsContractMetaData();
            BeanUtils.copyProperties(cmd, jcmd);
            jsContractMetaDatas.add(jcmd);
        });
        return jsContractMetaDatas;
    }

//    public static void main(String[] args) {
////        String template = "<p>您好，欢迎{$userName$},使用我们的{$contractName$},请选择交付方式{$rent_frequency$}</p>";
//        String template = "<p>您好，欢迎{$merchant_name$},使用我们的服务，您的电话{$company_mobile$},请选择交付方式{$rent_frequency$}</p>";
//        List<ContractMetaData> contractMetaDatas = Arrays.asList(
//                ContractMetaData.builder().code("company_mobile")
//                        .name("联系电话")
//                        .allowNull(0).dataType(1)
//                        .controlType(1)
//                        .defaultValue("13811111111")
//                        .build(),
//                ContractMetaData.builder().code("merchant_name")
//                        .name("商户名称").allowNull(1)
//                        .dataType(1).width(80)
//                        .controlType(1)
//                        .build(),
//                ContractMetaData.builder().code("rent_frequency")
//                        .name("租金每（季月年）一交").allowNull(0)
//                        .dataType(2)
//                        .controlType(2).dict("2,季;1,月;3,年")
//                        .defaultValue("3")
//                        .build()
//        );
//        String editeView = parseEditeView(template, contractMetaDatas);
//        System.out.println(editeView);
//
//        Map<String, Object> valueMap = new HashMap<>();
//        valueMap.put("company_mobile", "13811111111");
//        valueMap.put("merchant_name", "哈哈");
//        valueMap.put("rent_frequency", 3);
//        valueMap.get("a");
//        String readView = parseReadView(template, contractMetaDatas, valueMap);
//        System.out.println(readView);
//        //      System.out.println(getEditeCheckJs(contractMetaDatas));
//    }

//    @Data
//    public static class JsContractMetaData {
//        private String code;
//        private String name;
//        private Integer allowNull;
//        private int dataType;
//        private String checkRegex;
//    }

}
