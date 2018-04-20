package cn.com.maptrack2;

import java.util.HashMap;

public class SampleGattAttributes {
  public static String CLIENT_CHARACTERISTIC_CONFIG;
  public static String HEART_RATE_MEASUREMENT;
  public static String HEART_RATE_MEASUREMENT2;
  public static String RS232_CHARACTERISTIC;
  public static String USER_DEFINE_SERVICE;
  private static HashMap<String, String> attributes = new HashMap();


  
  static
  {
    HEART_RATE_MEASUREMENT = "0000ffe1-0000-1000-8000-00805f9b34fb";
    HEART_RATE_MEASUREMENT2 = "0000ffe2-0000-1000-8000-00805f9b34fb";
    CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    RS232_CHARACTERISTIC = "0000fff1-0000-1000-8000-00805f9b34fb";
    
    USER_DEFINE_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
    
    attributes.put(USER_DEFINE_SERVICE, "基于自定义通信协议的蓝牙服务");
    attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access Profile Service");
    attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute Profile Service");
    attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
    attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
    
    attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
    attributes.put(HEART_RATE_MEASUREMENT2, "Heart Rate Measurement2");
    attributes.put(CLIENT_CHARACTERISTIC_CONFIG, "client characteistic config");

    attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "工厂代码");
    attributes.put("0000fff6-0000-1000-8000-00805f9b34fb", "第一特征值");
    attributes.put("0000fff2-0000-1000-8000-00805f9b34fb", "第二特征值");
    attributes.put("0000fff3-0000-1000-8000-00805f9b34fb", "第三特征值");
    attributes.put("0000fff4-0000-1000-8000-00805f9b34fb", "第四特征值");
    attributes.put("0000fff5-0000-1000-8000-00805f9b34fb", "第五特征值");
    attributes.put(RS232_CHARACTERISTIC, "串口特征值");
  }

  public static String lookup(String uuid, String dft)
  {
    String str = (String)attributes.get(uuid);
    if (str == null){
    	str = dft;
    }
    return str;
  }
}
