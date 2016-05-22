package com.roadshr.cordova.lbs_trace;

import java.util.Map;
import java.util.HashMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Looper;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.LocationMode;
import com.baidu.trace.OnStartTraceListener;
import com.baidu.trace.OnStopTraceListener;
import com.baidu.trace.OnTrackListener;
import com.baidu.trace.Trace;
import com.baidu.trace.OnGeoFenceListener;
import com.baidu.trace.OnEntityListener;
import com.baidu.trace.TraceLocation;

public class LBSTracePlugin extends CordovaPlugin {
    private static LBSTracePlugin instance;
    /**
     * Log TAG
     */
    private static final String TAG = LBSTracePlugin.class.getSimpleName();

    /**
     * JS回调接口对象
     */
    public static CallbackContext startTraceCallbackContext = null;

    //实例化轨迹服务客户端
    public LBSTraceClient mtraceClient = null;

    //实例化轨迹服务
    private Trace trace = null;

    //位置采集周期
    final int gatherInterval = 5;
    //打包周期
    final int packInterval = 5;

    public LBSTracePlugin() {
        instance = this;
    }

    //鹰眼服务ID
    private long serviceId;

    private String entityName;

    private int traceType;

    /**
     * 插件初始化
     */
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        Log.d(TAG, "LBSTracePlugin#initialize");
        super.initialize(cordova, webView);
    }

    /**
     * 插件主入口
     */
    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "LBSTracePlugin#execute");

        boolean ret = false;
        if ("configure".equalsIgnoreCase(action)) {
            serviceId = args.getLong(0);
            entityName = args.getString(1);
            traceType = args.getInt(2);
            mtraceClient = new LBSTraceClient(cordova.getActivity().getApplicationContext());
            trace = new Trace(cordova.getActivity().getApplicationContext(), serviceId, entityName, traceType);

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    Log.d(TAG, Long.toString(serviceId));
                    //设置位置采集和打包周期
                    mtraceClient.setInterval(gatherInterval, packInterval);
                    // 设置定位模式
                    mtraceClient.setLocationMode(LocationMode.High_Accuracy);

                    mtraceClient.setOnTrackListener(trackListener);

                    mtraceClient.setProtocolType(0);
                    
                    mtraceClient.setOnEntityListener(entityListener);

                    PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                    pluginResult.setKeepCallback(false);
                    callbackContext.sendPluginResult(pluginResult);
                    Log.d(TAG, "LBSTracePlugin#configure");
                }
            });
            ret = true;
        }
        if ("startTrace".equalsIgnoreCase(action)) {
            startTraceCallbackContext = callbackContext;
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    Log.d(TAG, "LBSTracePlugin#start");
                    //开启轨迹服务
                    mtraceClient.startTrace(trace, startTraceListener);

                    PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                    pluginResult.setKeepCallback(true);
                    startTraceCallbackContext.sendPluginResult(pluginResult);
                }
            });
            ret = true;
        } else if ("stopTrace".equalsIgnoreCase(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    Log.d(TAG, "LBSTracePlugin#stop");
                    //停止轨迹服务
                    mtraceClient.stopTrace(trace, stopTraceListener);

                    PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                    pluginResult.setKeepCallback(false);
                    callbackContext.sendPluginResult(pluginResult);
                }
            });
            ret = true;
        } else if ("queryRealtimeLoc".equalsIgnoreCase(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    Log.d(TAG, "LBSTracePlugin#queryRealtimeLoc");
                    //停止轨迹服务
                    mtraceClient.queryRealtimeLoc(serviceId, entityListener);

                    PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                    pluginResult.setKeepCallback(false);
                    callbackContext.sendPluginResult(pluginResult);
                }
            });
            ret = true;
        } else if ("queryEntityList".equalsIgnoreCase(action)) {
            //entity标识列表（多个entityName，以英文逗号"," 分割）
            final String entityNames = args.getString(0);
            //检索条件（格式为 : "key1=value1,key2=value2,....."）
            final String columnKey = args.getString(1);
            //活跃时间，UNIX时间戳（指定该字段时，返回从该时间点之后仍有位置变动的entity的实时点集合）
            final int activeTime = args.getInt(2);

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    Log.d(TAG, "LBSTracePlugin#queryEntityList");
                    //返回结果的类型（0 : 返回全部结果，1 : 只返回entityName的列表）
                    int returnType = 0;
                    //分页大小
                    int pageSize = 1000;
                    //分页索引
                    int pageIndex = 1;
                    //查询实时轨迹
                    mtraceClient.queryEntityList(serviceId, entityNames, columnKey, returnType, activeTime, pageSize,
                            pageIndex, entityListener);

                    PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                    pluginResult.setKeepCallback(false);
                    callbackContext.sendPluginResult(pluginResult);
                }
            });
            ret = true;
        } else if ("queryHistoryTrack".equalsIgnoreCase(action)) {
            //开始时间（Unix时间戳）
            final int startTime = args.getInt(0);
            //结束时间（Unix时间戳）
            final int endTime = args.getInt(1);

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    Log.d(TAG, "LBSTracePlugin#queryHistoryTrack");
                    //是否返回精简的结果（0 : 将只返回经纬度，1 : 将返回经纬度及其他属性信息）
                    int simpleReturn = 1;
                    //分页大小
                    int pageSize = 1000;
                    //分页索引
                    int pageIndex = 1;
                    //查询历史轨迹
                    mtraceClient.queryHistoryTrack(serviceId, entityName, simpleReturn, startTime, endTime, pageSize, pageIndex,trackListener);

                    PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                    pluginResult.setKeepCallback(false);
                    callbackContext.sendPluginResult(pluginResult);
                }
            });
            ret = true;
        }
        return ret;
    }

    /**
     * 接收推送成功内容并返回给前端JS
     *
     * @param jsonObject JSON对象
     */
    private void sendSuccessData(CallbackContext callbackContext, JSONObject jsonObject, boolean isCallBackKeep) {
        Log.d(TAG, "LBSTracePlugin#sendSuccessData: " + (jsonObject != null ? jsonObject.toString() : "null"));

        if (callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, jsonObject);
            result.setKeepCallback(isCallBackKeep);
            callbackContext.sendPluginResult(result);
        }
    }

    /**
     * 接收推送失败内容并返回给前端JS
     *
     * @param jsonObject JSON对象
     */
    private void sendErrorData(CallbackContext callbackContext, JSONObject jsonObject, boolean isCallBackKeep) {
        Log.d(TAG, "LBSTracePlugin#sendErrorData: " + (jsonObject != null ? jsonObject.toString() : "null"));

        if (callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, jsonObject);
            result.setKeepCallback(false);
            callbackContext.sendPluginResult(result);
        }
    }

    //实例化开启轨迹服务回调接口
    OnStartTraceListener startTraceListener = new OnStartTraceListener() {
        //开启轨迹服务回调接口（arg0 : 消息编码，arg1 : 消息内容，详情查看类参考）
        @Override
        public void onTraceCallback(int arg0, String arg1) {
            Log.d(TAG, "onTraceCallback");
        }

        //轨迹服务推送接口（用于接收服务端推送消息，arg0 : 消息类型，arg1 : 消息内容，详情查看类参考）
        @Override
        public void onTracePushCallback(byte arg0, String arg1) {
            Log.d(TAG, "onTracePushCallback");
        }
    };

    //实例化停止轨迹服务回调接口
    OnStopTraceListener stopTraceListener = new OnStopTraceListener() {
        // 轨迹服务停止成功
        @Override
        public void onStopTraceSuccess() {
            Log.d(TAG, "onStopTraceSuccess");

        }

        // 轨迹服务停止失败（arg0 : 错误编码，arg1 : 消息内容，详情查看类参考）
        @Override
        public void onStopTraceFailed(int arg0, String arg1) {
            Log.d(TAG, "onStopTraceFailed");
        }
    };

    OnTrackListener trackListener = new OnTrackListener() {

        // 请求失败回调接口
        @Override
        public void onRequestFailedCallback(String arg0) {
            Looper.prepare();
            Log.d(TAG, "onRequestFailedCallback" + arg0);
            Looper.loop();
        }

        // 查询历史轨迹回调接口
        @Override
        public void onQueryHistoryTrackCallback(String arg0) {
            super.onQueryHistoryTrackCallback(arg0);
            Log.d(TAG, "onQueryHistoryTrackCallback" + arg0);
        }

        //轨迹属性回调接口
        // @Override
        // public Map<String, String> onTrackAttrCallback() {
        //     Map<String, String> ret = new HashMap<String, String>();
        //     return  ret;
        // }
    };

    //Entity监听器
    OnEntityListener entityListener = new OnEntityListener() {
        // 查询失败回调接口
        @Override
        public void onRequestFailedCallback(String arg0) {
            Log.d(TAG, "onRequestFailedCallback : " + arg0);
        }

        // 查询entity回调接口，返回查询结果列表
        @Override
        public void onQueryEntityListCallback(String arg0) {
            Log.d(TAG, "onQueryEntityListCallback : " + arg0);
        }

        @Override
        public void onAddEntityCallback(String s) {
            super.onAddEntityCallback(s);
            Log.d(TAG, "onAddEntityCallback : " + s);
        }

        @Override
        public void onUpdateEntityCallback(String s) {
            super.onUpdateEntityCallback(s);
            Log.d(TAG, "onUpdateEntityCallback : " + s);
        }

        @Override
        public void onReceiveLocation(TraceLocation traceLocation) {
            super.onReceiveLocation(traceLocation);
            Log.d(TAG, "onReceiveLocation : " + traceLocation);
        }
    };
}