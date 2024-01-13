package cn.master.zeus.common.constants;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Created by 11's papa on 01/12/2024
 **/
@Getter
public enum CustomFieldType {
    INPUT("input", false),
    TEXTAREA("textarea", false),
    SELECT("select", true),
    MULTIPLE_SELECT("multipleSelect", true),
    RADIO("radio", true),
    CHECKBOX("checkbox", true),
    MEMBER("member", true),
    MULTIPLE_MEMBER("multipleMember", true),
    DATE("date", false),
    DATETIME("datetime", false),
    INT("int", false),
    FLOAT("float", false),
    MULTIPLE_INPUT("multipleInput", false),
    RICH_TEXT("richText", false),
    CASCADING_SELECT("cascadingSelect", false);

    private final String value;
    private final Boolean hasOption;

    CustomFieldType(String value, Boolean hasOption) {
        this.value = value;
        this.hasOption = hasOption;
    }

    public static Set<String> getHasOptionValueSet() {
        return Arrays.stream(CustomFieldType.values())
                .filter(CustomFieldType::getHasOption)
                .map(CustomFieldType::getValue)
                .collect(Collectors.toSet());
    }

}
