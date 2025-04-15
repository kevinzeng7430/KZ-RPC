package com.kz.rpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageTypeEnum {

    REQUEST(0),
    RESPONSE(1),
    HEART_BEAT(2),
    OTHER(3);

    private final int key;
    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }
    /**
     * 获取key
     *
     * @return key
     */
    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum type : ProtocolMessageTypeEnum.values()) {
            if (type.key == key) {
                return type;
            }
        }
        return null;
    }

}
