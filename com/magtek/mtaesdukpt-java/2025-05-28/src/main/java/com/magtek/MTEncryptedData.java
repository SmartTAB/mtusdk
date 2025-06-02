 package com.magtek;
 
 public class MTEncryptedData{
     protected long nativeClass = 0;

     // Load the native library
     static {
         System.loadLibrary("MTAESDUKPTJ"); // Load the compiled C library, e.g., libnative-lib.so or native-lib.dll
         //System.load("/Users/Yong/Downloads/UniversalSDK_Linux/MTAESDUKPTJ/libMTAESDUKPTJ.so");
     }

     public MTEncryptedData() {
         nativeClass = 0;
     }
     public MTEncryptedData(String key) {
         nativeClass = Init(key);
     }

     public String getSelectedCardData(String arqc) {
         return GetSelectedCardData(nativeClass, arqc);
     }

     protected void finalize()
     {
         if (nativeClass != 0) {
             Deinit(nativeClass);
         }
     }
 

     // Native method declaration
     public native long Init(String key);
     public native String GetSelectedCardData(long cls, String arqc);
     public native long Deinit(long cls);

     public static void main(String[] args) {
         String arqc = "01FAF98201F6DFDF540A9070040BE0010A400005DFDF550182DFDF250742453030313041FA8201D4708201D0820239009F6E0708400000303000DFDF530100DFDF4D273B353434333030303034303030333435353D30303030303030303030303030303030303030303FDFDF520105FF4282004EDFDFDF37820030ABBA1BBCDFA0D9A1DBB90676186863DAC6D85A47302778D0AB1829842AB34B5C99113DB245DF0F51052D982C6EDCAAEADFDFDF380CA1170005BE0010A00000000BDFDFDF39019DF8820136DFDF59820118878160B922DEF84C7FC6F2BFA3F5590D5DE033A29369424AC4DFAFDD9F125D4D9BEC2D321632F95D1B8E87B4DA3B76769274CEB54A1CF0873D30F6AAE5348AC40E773B0E60BFC48776482FC949ECF49C52CC7BA284F63EBD885EEB5FC36764E9665008ADDDF2E51C0B8A0723E10EA64E1D427154B0CF38F4751AC32EE0F8C8AD9B0E48D6D1BF53B5F862C9257452A22710EBC39C44C4314B6484F9BB292B3C2E1693C6EDAF135C9D516D6E828ED1CC076F3549ACBDE79CEC74F1FCE390D64912B5B51DEFE090797A1FBE0F73C51D3546636100869C65C4FB106C7371810AF0CA6EC74095559136B51475E32778F942A893296E50845F749A3D31EBC7CEB766E22B14AAA477B834D996C69324FB7568369DAB0BE43CF60566DFDF560A9070040BE0010A400005DFDF570180DFDF580106000000006453305B";
         String ansiKey = "FEDCBA9876543210F1F1F1F1F1F1F1F1";
         
         MTEncryptedData test1 = new MTEncryptedData();
         long cls1 = test1.Init(ansiKey);
         String result = test1.GetSelectedCardData(cls1, arqc);
         System.out.println(result);
         test1.Deinit(cls1);
         

         MTEncryptedData test2 = new MTEncryptedData(ansiKey);
         String result2 = test2.getSelectedCardData(arqc);
         System.out.println(result2);
     }
 }