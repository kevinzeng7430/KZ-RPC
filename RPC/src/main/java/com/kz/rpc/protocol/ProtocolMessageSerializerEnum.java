package com.kz.rpc.protocol;


import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum ProtocolMessageSerializerEnum {
    JDK(0, "jdk"),
    JSON(1, "json"),
    HESSIAN(2, "hessian"),
    KRYOS(3, "kryo");

    private final int key;
    private final String value;
    ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }
    /**
     * 获取值列表
     */
    public static List<String> getValues(){
        return Arrays.stream(values())
                .map(ProtocolMessageSerializerEnum::getValue)
                .toList();
    }
    /**
     * 根据key获取枚举
     *
     * @return key
     */
    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum type : ProtocolMessageSerializerEnum.values()) {
            if (type.key == key) {
                return type;
            }
        }
        return null;
    }
    /**
     * 根据值获取枚举
     *
     * @return value
     */
    public static ProtocolMessageSerializerEnum getEnumByValue(String value) {
       if(ObjectUtil.isEmpty(value)){
           return null;
       }
        for (ProtocolMessageSerializerEnum type : ProtocolMessageSerializerEnum.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}
