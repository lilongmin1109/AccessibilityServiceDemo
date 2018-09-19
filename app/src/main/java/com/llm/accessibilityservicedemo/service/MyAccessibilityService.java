package com.llm.accessibilityservicedemo.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.llm.accessibilityservicedemo.util.Config;
import com.llm.accessibilityservicedemo.util.PhoneController;
import com.llm.accessibilityservicedemo.util.UI;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Longmin Li on 2018/8/8.
 */

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "llm";

    private Handler handler = new Handler();
    private boolean hasNotify = false;

    /**
     * 接收所监听的事件
     *
     * @param event
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String eventTypeName = "";
        switch (eventType) {
            //监听通知栏发生改变
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                eventTypeName = "TYPE_NOTIFICATION_STATE_CHANGED";

                if (PhoneController.isLockScreen(this)) { // 锁屏
                    PhoneController.unlock(this);   // 唤醒点亮屏幕  //TODO 没有效果
                }
                openAppByNotification(event);
                hasNotify = true;

                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED://点击事件
                eventTypeName = "TYPE_VIEW_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventTypeName = "TYPE_VIEW_FOCUSED";
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                eventTypeName = "TYPE_VIEW_LONG_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                eventTypeName = "TYPE_VIEW_SELECTED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                eventTypeName = "TYPE_VIEW_TEXT_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://窗体状态改变
                eventTypeName = "TYPE_WINDOW_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                eventTypeName = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                eventTypeName = "TYPE_ANNOUNCEMENT";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                eventTypeName = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                eventTypeName = "TYPE_VIEW_HOVER_ENTER";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                eventTypeName = "TYPE_VIEW_HOVER_EXIT";
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED://界面滚动
                eventTypeName = "TYPE_VIEW_SCROLLED";

                //如果在当前界面直接发送消息
//                sendMessage();

                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                eventTypeName = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED://窗体内容更改
                eventTypeName = "TYPE_WINDOW_CONTENT_CHANGED";

                //微信自动回复
                autoReply();

                break;
            default:

                break;
        }
//        Log.e(TAG, "eventType:" + eventType);
        Log.e(TAG, "eventTypeName:" + eventTypeName);
//        Log.e(TAG, "onAccessibilityEvent: " + event.toString());

        EventBus.getDefault().post(eventTypeName);
    }

    @Override
    public void onInterrupt() {

    }


    /**
     * 查找最后一条聊天信息
     *
     * @param rootNode
     * @param reply
     * @return
     */
    private boolean findLastTextStr(AccessibilityNodeInfo rootNode, String reply) {
        int count = rootNode.getChildCount();
        Log.e(TAG, "LastText root class=" + rootNode.getClassName() + ", " + rootNode.getText() + ", child: " + count);
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo node = rootNode.getChild(i);
            if (UI.EDITTEXT.equals(node.getClassName())) {   // 找到输入框并输入文本
                Log.e(TAG, "****found the EditText");
                fillText(node, reply);
                return true;
            }

            if (findInputBar(node, reply)) {    // 递归查找
                return true;
            }
        }
        return false;
    }


    /**
     * 自动回复入口
     */
    private void autoReply() {
        if (hasNotify) {
            try {
                Thread.sleep(1000); // 停1秒, 否则在微信主界面没进入聊天界面就执行了fillInputBar
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendMessage();
        }
    }

    /**
     * 填充信息与点击发送按钮
     */
    private void sendMessage() {
        if (fillInputBar(Config.AutoReplyText)) {
            //查找发送按钮并点击
            findAndPerformAction(UI.BUTTON, "发送");
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);   // 返回
//                }
//            }, 800);

        }
        hasNotify = false;
    }

    /**
     * 打开微信
     *
     * @param event 事件
     */
    private void openAppByNotification(AccessibilityEvent event) {
        if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            try {
                PendingIntent pendingIntent = notification.contentIntent;
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 填充输入框
     */
    private boolean fillInputBar(String reply) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            return findInputBar(rootNode, reply);
        }
        return false;
    }


    /**
     * 查找EditText控件
     *
     * @param rootNode 根结点
     * @param reply    回复内容
     * @return 找到返回true, 否则返回false
     */
    private boolean findInputBar(AccessibilityNodeInfo rootNode, String reply) {
        int count = rootNode.getChildCount();
        Log.e(TAG, "root class=" + rootNode.getClassName() + ", " + rootNode.getText() + ", child: " + count);
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo node = rootNode.getChild(i);
            if (UI.EDITTEXT.equals(node.getClassName())) {   // 找到输入框并输入文本
                Log.e(TAG, "****found the EditText");
                fillText(node, reply);
                return true;
            }

            if (findInputBar(node, reply)) {    // 递归查找
                return true;
            }
        }
        return false;
    }


    /**
     * 填充文本
     */
    private void fillText(AccessibilityNodeInfo nodeInfo, String reply) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.e(TAG, "set text");
            Bundle bundle = new Bundle();
            bundle.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    reply);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, bundle);
        } else {
            ClipData data = ClipData.newPlainText("reply", reply);
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(data);
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS); // 获取焦点
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE); // 执行粘贴
        }
    }


    /**
     * 查找UI控件并点击
     *
     * @param widget 控件完整名称, 如android.widget.Button, android.widget.TextView
     * @param text   控件文本
     */
    private void findAndPerformAction(String widget, String text) {
        // 取得当前激活窗体的根节点
        if (getRootInActiveWindow() == null) {
            return;
        }

        // 通过文本找到当前的节点
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        if (nodes != null) {
            for (AccessibilityNodeInfo node : nodes) {
                if (node.getClassName().equals(widget) && node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK); // 执行点击
                    break;
                }
            }
        }
    }


    private void findAndPerformActionButton(String text) {
        if (getRootInActiveWindow() == null)//取得当前激活窗体的根节点
            return;
        //通过文字找到当前的节点
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText("开启辅助功能");
        if (nodes != null && nodes.size() > 0)
            for (int i = 0; i < nodes.size(); i++) {
                AccessibilityNodeInfo node = nodes.get(i);
                // 执行点击行为
                if (node.getClassName().equals("android.widget.TextView") && node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
    }


    /**
     * 关闭辅助功能
     */
    private void closeService() {

    }

}
