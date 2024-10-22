package com.magtek.mtusdk;

import com.magtek.mtusdk.*;
import com.magtek.mtusdk.common.TLVParser;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public final class WrapperIDevice implements IDevice {
    protected long deviceHandle;
    protected WrapperEventSubscriber eventHandler = new WrapperEventSubscriber();

    protected static native String GetName(long var0);

    protected static native String GetConnectionInfo(long var0);

    protected static native long GetConnectionState(long var0);

    protected static native String GetDeviceInfo(long var0);

    protected static native String GetDeviceCapabilities(long var0);

    protected static native long GetDeviceControl(long var0);

    protected static native long GetDeviceConfiguration(long var0);

    protected static native long SubscribeAll(long var0, Object var2);

    protected static native long UnsubscribeAll(long var0, Object var2);

    protected static native boolean StartTransaction(long var0, String var2);

    protected static native boolean CancelTransaction(long var0);

    protected static native boolean SendSelection(long var0, String var2);

    protected static native boolean SendAuthorization(long var0, String var2);

    protected static native boolean RequestPIN(long var0, String var2);

    protected static native boolean RequestSignature(long var0);

    public WrapperIDevice(long handle) {
        this.deviceHandle = handle;
        this.eventHandler.onDeviceConnect = this::OnDeviceConnect;
        SubscribeAll(this.deviceHandle, this.eventHandler);
    }

    protected void finalize() {
        if (this.deviceHandle != 0L) {
            UnsubscribeAll(this.deviceHandle, this.eventHandler);
        }

    }

    protected void OnDeviceConnect(String data) {
        if (data != "connected" && data == "disconnected") {
            this.getDeviceControl().close();
        }

    }

    public String toString() {
        return this.Name();
    }

    public String Name() {
        return GetName(this.deviceHandle);
    }

    public ConnectionInfo getConnectionInfo() {
        String result = GetConnectionInfo(this.deviceHandle);
        Object json = JSONValue.parse(result);
        JSONObject obj = (JSONObject) json;
        DeviceType dType = DeviceType.MMS;
        int iDeviceType = ((Long) obj.get("DeviceType")).intValue();
        if (iDeviceType == 0) {
            dType = DeviceType.SCRA;
        } else if (iDeviceType == 1) {
            dType = DeviceType.PPSCRA;
        } else if (iDeviceType == 2) {
            dType = DeviceType.CMF;
        }

        ConnectionType cType = ConnectionType.USB;
        int iConnectionType = ((Long) obj.get("ConnectionType")).intValue();
        if (iConnectionType == 0) {
            cType = ConnectionType.USB;
        } else if (iConnectionType == 1) {
            cType = ConnectionType.BLUETOOTH_LE;
        } else if (iConnectionType == 2) {
            cType = ConnectionType.BLUETOOTH_LE_EMV;
        } else if (iConnectionType == 3) {
            cType = ConnectionType.BLUETOOTH_LE_EMVT;
        } else if (iConnectionType == 4) {
            cType = ConnectionType.TCP;
        } else if (iConnectionType == 5) {
            cType = ConnectionType.TCP_TLS;
        } else if (iConnectionType == 6) {
            cType = ConnectionType.TCP_TLS_TRUST;
        } else if (iConnectionType == 7) {
            cType = ConnectionType.SERIAL;
        } else if (iConnectionType == 1) {
            cType = ConnectionType.VIRTUAL;
        }

        String address = (String) obj.get("Address");
        return new ConnectionInfo(dType, cType, address);
    }

    public ConnectionState getConnectionState() {
        long state = GetConnectionState(this.deviceHandle);
        ConnectionState s = ConnectionState.Unknown;
        if (state == 0L) {
            s = ConnectionState.Unknown;
        } else if (state == 1L) {
            s = ConnectionState.Disconnected;
        } else if (state == 2L) {
            s = ConnectionState.Connecting;
        } else if (state == 3L) {
            s = ConnectionState.Error;
        } else if (state == 4L) {
            s = ConnectionState.Connected;
        } else if (state == 5L) {
            s = ConnectionState.Disconnecting;
        }

        return s;
    }

    public DeviceInfo getDeviceInfo() {
        String info = GetDeviceInfo(this.deviceHandle);
        Object json = JSONValue.parse(info);
        JSONObject obj = (JSONObject) json;
        String model = (String) obj.get("Model");
        String name = (String) obj.get("Name");
        String serial = (String) obj.get("Serial");
        return new DeviceInfo(name, model, serial);
    }

    public IDeviceCapabilities getCapabilities() {
        try {
            String info = GetDeviceCapabilities(this.deviceHandle);
            return new WrapperDeviceCapabilities(info);
        } catch (Exception var2) {
            return null;
        }
    }

    public IDeviceControl getDeviceControl() {
        long deviceControlHandle = GetDeviceControl(this.deviceHandle);
        return new WrapperIDeviceControl(deviceControlHandle);
    }

    public boolean subscribeAll(IEventSubscriber eventCallback) {
        this.eventHandler.target = eventCallback;
        return true;
    }

    public boolean unsubscribeAll(IEventSubscriber eventCallback) {
        this.eventHandler.target = null;
        return false;
    }

    public boolean startTransaction(ITransaction transaction) {
        JSONObject json = new JSONObject();
        json.put("Amount", transaction.Amount());
        json.put("CashBack", transaction.CashBack());
        json.put("EMVOnly", transaction.EMVOnly());
        int payments = 0;
        if (transaction.PaymentMethods().contains(PaymentMethod.MSR)) {
            ++payments;
        }

        if (transaction.PaymentMethods().contains(PaymentMethod.Contact)) {
            payments += 2;
        }

        if (transaction.PaymentMethods().contains(PaymentMethod.Contactless)) {
            payments += 4;
        }

        if (transaction.PaymentMethods().contains(PaymentMethod.ManualEntry)) {
            payments += 8;
        }

        json.put("PaymentMethods", payments);
        json.put("QuickChip", transaction.QuickChip());
        json.put("Timeout", transaction.Timeout());
        json.put("PreventMSRSignatureForCardWithICC", transaction.PreventMSRSignatureForCardWithICC());
        json.put("EMVResponseFormat", transaction.EMVResponseFormat());
        json.put("TransactionType", transaction.TransactionType());
        json.put("CurrencyCode", TLVParser.getHexString(transaction.CurrencyCode()));
        json.put("CurrencyExponent", transaction.CurrencyExponent());
        json.put("TransactionCategory", transaction.TransactionCategory());
        json.put("MerchantCategory", transaction.MerchantCategory());
        json.put("MerchantID", transaction.MerchantID());
        json.put("MerchantCustomData", transaction.MerchantCustomData());
        String jsonString = json.toJSONString();
        return StartTransaction(this.deviceHandle, jsonString);
    }

    public boolean cancelTransaction() {
        return CancelTransaction(this.deviceHandle);
    }

    public boolean sendSelection(IData data) {
        return SendSelection(this.deviceHandle, data.StringValue());
    }

    public boolean sendAuthorization(IData data) {
        return SendAuthorization(this.deviceHandle, data.StringValue());
    }

    public boolean requestPIN(PINRequest request) {
        JSONObject json = new JSONObject();
        json.put("Timeout", request.getTimeout());
        json.put("PINMode", request.getPINMode());
        json.put("MinLength", request.getMinLength());
        json.put("MaxLength", request.getMaxLength());
        json.put("Tone", request.getTone());
        json.put("Format", request.getFormat());
        json.put("PAN", request.getPAN());
        return RequestPIN(this.deviceHandle, json.toJSONString());
    }

    public boolean requestSignature() {
        return RequestSignature(this.deviceHandle);
    }

    public IDeviceConfiguration getDeviceConfiguration() {
        long deviceControlHandle = GetDeviceConfiguration(this.deviceHandle);
        return new WrapperIDeviceConfiguration(deviceControlHandle);
    }

    static byte[] getByteArrayFromHexString(String hexString) {
        int byteLength = 2;
        byte[] result = null;
        if (hexString != null) {
            result = new byte[hexString.length() / byteLength];
            char[] hexCharArray = hexString.toUpperCase().toCharArray();

            for (int i = 0; i < result.length; ++i) {
                StringBuffer sbCurrent = new StringBuffer("");
                sbCurrent.append(String.valueOf(hexCharArray[i * byteLength]));
                sbCurrent.append(String.valueOf(hexCharArray[i * byteLength + 1]));

                try {
                    result[i] = (byte) Integer.parseInt(sbCurrent.toString(), 16);
                } catch (Exception var7) {
                }
            }
        }

        return result;
    }
}
