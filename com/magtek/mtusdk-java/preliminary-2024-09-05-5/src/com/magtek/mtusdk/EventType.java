package com.magtek.mtusdk;

// DO NOT change package
public enum EventType {
    Invalid(-1),

    ConnectionState(0),

    DeviceResponse(1),

    DeviceExtendedResponse(2),

    DeviceNotification(3),

    DataTransferCancelled(4),

    CardData(5),

    TransactionStatus(6),

    DisplayMessage(7),

    InputRequest(8),

    AuthorizationRequest(9),

    TransactionResult(10),

    PINBlock(11),

    Signature(12),

    DeviceDataFile(13),

    OperationStatus(14),

    DeviceEvent(15),

    UserEvent(16),

    FeatureStatus(17),

    PINData(18),

    PANData(19),

    BarCodeData(20),

    NFCEvent(21), //NFCEvent,

    NFCData(22), //NFCData,

    NFCResponse(23); //NFC Response

    private long lValue;

    private EventType(long value) {
        this.lValue = value;
    }

    public long toLong() {
        return this.lValue;
    }


    static EventType fromLong(long value) {
        EventType[] As = values();

        for (int i = 0; i < As.length; ++i) {
            if (As[i].lValue == value) {
                return As[i];
            }
        }

        return Invalid;
    }

}
