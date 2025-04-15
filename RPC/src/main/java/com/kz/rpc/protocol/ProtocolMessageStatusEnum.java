package com.kz.rpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageStatusEnum {
        OK("OK", 200),
        BAD_REQUEST("BAD_REQUEST", 400),
        BAD_RESPONSE("BAD_RESPONSE", 500);

        private final String text;
        private final int value;

        ProtocolMessageStatusEnum(String text, int value) {
            this.text = text;
            this.value = value;
        }
        /**
         * 根据值获取枚举
         *
         * @param value 值
         * @return 枚举
         */
        public static ProtocolMessageStatusEnum getEnumByValue(int value) {
            for (ProtocolMessageStatusEnum status : ProtocolMessageStatusEnum.values()) {
                if (status.value == value) {
                    return status;
                }
            }
            return null;
        }
}
