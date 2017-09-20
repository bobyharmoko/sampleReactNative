package com.sid.EscDriver;

import android.widget.Toast;
import android.util.Log;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;

import java.util.Map;
import java.util.HashMap;
import android.content.Context;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.ArrayList;

public class SidEscDriver extends ReactContextBaseJavaModule {

  private static final String DURATION_SHORT_KEY = "SHORT";
  private static final String DURATION_LONG_KEY = "LONG";

  public SidEscDriver(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "SidEscDriver";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
    constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
    return constants;
  }

  @ReactMethod
  public void show(String message, int duration) {
    Toast.makeText(getReactApplicationContext(), message, duration).show();
  }

  private String  _IpAdress = null;
  private int  _Port;

  private Socket mSocket = null;
  private PrintWriter mPrinter = null;

  private int _maxLength;

  private int  f_size = 1;

  @ReactMethod
  public void SetInitial(String IpAdress, int Port, int maxLength){
    _IpAdress = IpAdress;
    _Port = 9100;
    _maxLength = maxLength ;
  }

  private ArrayList<myData> myList = new ArrayList<myData>();

  @ReactMethod
  public void AddText(String txt){
      myList.add(new myData("AddText", txt));
  }


  @ReactMethod
  public void AddFeed(String txt){
      myList.add(new myData("AddFeed", txt));
  }

  @ReactMethod
  public void AddCut(){
      myList.add(new myData("AddCut", ""));
  }

  private void doWriteText(PrintWriter _print, String txt){
      String str = txt;
      _print.println(str.toCharArray());
  }

  private void doWriteFeed(PrintWriter _print, int feed){
      _print.write(0x1B);
      _print.write("d");
      _print.write(feed);
  }

  @ReactMethod
  public void Sent(Promise promise){
        try {
          Log.i("info", "Starting");
          Log.i("info", _IpAdress);
          
          //mSocket = new Socket(_IpAdress, _Port);
          mSocket = new Socket();
          mSocket.connect(new InetSocketAddress(_IpAdress, _Port), 1500);
          mPrinter = new PrintWriter(mSocket.getOutputStream());
          for (myData object: myList) {
              if(object.method == "AddText"){
                  doWriteText(mPrinter, object.value);
              }else if(object.method == "AddFeed"){
                  doWriteFeed(mPrinter, Integer.parseInt(object.value));
              }else if(object.method=="AddCut"){
                  mPrinter.println(new char[]{0x1D, 0x56, 0x42, 0x00});
              }
          }
          mPrinter.flush();
          mPrinter.close();
          mSocket.close();
          myList = new ArrayList<myData>();
          Log.i("info", "Success");
        //  successCallBack.invoke();
            promise.resolve(true);
    }catch (IOException e){
        Log.v("err", "Error");
        Log.v("err", e.toString());
          myList = new ArrayList<myData>();
        //e.printStackTrace();
        //errorCallback.invoke(e.toString());
        promise.reject("ERR", e.toString());
    }

  }

}

class myData {
    public String method = null;
    public String value = null;
    //constructor
    public myData(String _method, String _value) {
        method = _method;
        value = _value;
    }
}
