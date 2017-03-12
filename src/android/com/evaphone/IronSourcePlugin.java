package com.evaphone;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;


import com.ironsource.adapters.supersonicads.SupersonicConfig;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.InterstitialPlacement;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;

import java.util.Date;

public class IronSourcePlugin extends CordovaPlugin implements RewardedVideoListener, OfferwallListener, InterstitialListener {
  private static final String TAG = "IronSourcePlugin";
  private CallbackContext eventContext;

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    Log.d(TAG, "Initializing IronSourcePlugin");
  }


  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

    if(action.equals("init")) {
      String appKey = args.getString(0);
      String userId = args.getString(1);
      return this.init(appKey, userId, callbackContext);
    } else if(action.equals("showRewardedVideo")) {
      String placementName = null;
      if (args.length() == 1) {
        placementName = args.getString(0);
      }
      return this.showRewardedVideo(placementName);
    } else if(action.equals("getRewardedVideoPlacementInfo")) {
      String placementName = args.getString(0);
      return this.getRewardedVideoPlacementInfo(placementName);
    } else if(action.equals("isRewardedVideoPlacementCapped")) {
      String placementName = args.getString(0);
      return this.isRewardedVideoPlacementCapped(placementName);
    } else if(action.equals("setDynamicUserId")) {
      String userId = args.getString(0);
      return this.setDynamicUserId(userId);
    } else if(action.equals("loadInterstitial")) {
      return this.loadInterstitial();
    } else if(action.equals("isInterstitialReady")) {
      return this.isInterstitialReady();
    } else if(action.equals("getInterstitialPlacementInfo")) {
      String placementName = args.getString(0);
      return this.getInterstitialPlacementInfo(placementName);
    } else if(action.equals("showInterstitial")) {
      String placementName = null;
      if (args.length() == 1) {
        placementName = args.getString(0);
      }
      return this.showInterstitial(placementName);
    } else if(action.equals("showOfferwall")) {
      String placementName = null;
      if (args.length() == 1) {
        placementName = args.getString(0);
      }
      return this.showOfferwall(placementName);
    }
    return true;
  }

  private boolean init(String appKey, String userId, CallbackContext callbackContext) {
    if (eventContext == null) {
      eventContext = callbackContext;
    }
    IronSource.setRewardedVideoListener(this);
    IronSource.setOfferwallListener(this);
    SupersonicConfig.getConfigObj().setClientSideCallbacks(true);
    IronSource.setInterstitialListener(this);
    IronSource.setUserId(userId);
    IronSource.init(this.cordova.getActivity(), appKey);
    return true;
  }

  private boolean showRewardedVideo(String placementName) {
    IronSource.showRewardedVideo(placementName);
    return true;
  }

  private boolean getRewardedVideoPlacementInfo(String placementName) {
    Placement placement = IronSource.getRewardedVideoPlacementInfo(placementName);
    if (placement != null) {
      JSONObject event = new JSONObject();
      try {
        event.put("rewardName", placement.getRewardName());
        event.put("rewardAmount", placement.getRewardAmount());
      } catch (JSONException e) {
        e.printStackTrace();
      }
      eventContext.success(event);
    }
    else {
      eventContext.error("placementName_invalid");
    }
    return true;
  }

  private boolean isRewardedVideoPlacementCapped(String placementName) {
    boolean isCapped = IronSource.isRewardedVideoPlacementCapped("placementName");
    if (isCapped) {
      eventContext.error("capped");
    }
    else {
      eventContext.success("ok");
    }
    return true;
  }

  private boolean setDynamicUserId(String userId) {
    IronSource.setDynamicUserId(userId);
    return true;
  }

  private boolean loadInterstitial() {
    IronSource.loadInterstitial();
    return true;
  }

  private boolean isInterstitialReady() {
    boolean isReady = IronSource.isInterstitialReady();
    if (isReady) {
      eventContext.success("ready");
    }
    else {
      eventContext.error("not_ready");
    }
    return true;
  }

  private boolean getInterstitialPlacementInfo(String placementName) {
    InterstitialPlacement placement = IronSource.getInterstitialPlacementInfo(placementName);
    if (placement != null) {
      JSONObject event = new JSONObject();
      try {
        event.put("placementName", placement.getPlacementName());
        event.put("placementId", placement.getPlacementId());
      } catch (JSONException e) {
        e.printStackTrace();
      }
      eventContext.success(event);
    }
    else {
      eventContext.error("placementName_invalid");
    }
    return true;
  }


  private boolean showInterstitial(String placementName) {
    IronSource.showInterstitial(placementName);
    return true;
  }

  private boolean showOfferwall(String placementName) {
    IronSource.showOfferwall(placementName);
    return true;
  }



  @Override
  public void onPause(boolean multitasking) {
    super.onPause(multitasking);
    IronSource.onPause(this.cordova.getActivity());
  }

  @Override
  public void onResume(boolean multitasking) {
    super.onResume(multitasking);
    IronSource.onResume(this.cordova.getActivity());
  }


  private void raiseEvent(String type) {
    raiseEvent(type, null);
  }

  private void raiseEvent(String type, Object data) {

    if (eventContext != null) {

      JSONObject event = new JSONObject();
      try {
        event.put("type", type);
        event.put("data", data);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      PluginResult result = new PluginResult(PluginResult.Status.OK, event);
      result.setKeepCallback(true);
      eventContext.sendPluginResult(result);
    }
  }

  // --------- IronSource Rewarded Video Listener ---------
  @Override
  public void onRewardedVideoAdOpened() {
    this.raiseEvent("onRewardedVideoAdOpened");
  }

  @Override
  public void onRewardedVideoAdClosed() {
    this.raiseEvent("onRewardedVideoAdClosed");
  }

  @Override
  public void onRewardedVideoAvailabilityChanged(boolean b) {
    final JSONObject data = new JSONObject();
    try {
      data.put("videoAvailable", b);
    } catch (JSONException e) {
    }
    this.raiseEvent("onRewardedVideoAvailabilityChanged", data);
  }

  @Override
  public void onRewardedVideoAdStarted() {
    this.raiseEvent("onRewardedVideoAdStarted");
  }

  @Override
  public void onRewardedVideoAdEnded() {
    this.raiseEvent("onRewardedVideoAdEnded");
  }

  @Override
  public void onRewardedVideoAdRewarded(Placement placement) {
    final JSONObject data = new JSONObject();
    try {
      data.put("placementName", placement.getPlacementName());
      data.put("rewardName", placement.getRewardName());
      data.put("rewardAmount", placement.getRewardAmount());
    } catch (JSONException e) {
    }
    this.raiseEvent("onRewardedVideoAdRewarded", data);
  }

  @Override
  public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {
    final JSONObject data = new JSONObject();
    try {
      data.put("errorCode", ironSourceError.getErrorCode());
      data.put("errorMessage", ironSourceError.getErrorMessage());
    } catch (JSONException e) {
    }
    this.raiseEvent("onRewardedVideoAdShowFailed", data);
  }

  // --------- IronSource Offerwall Listener ---------


  @Override
  public void onOfferwallAvailable(boolean b) {
    final JSONObject data = new JSONObject();
    try {
      data.put("offerAvailable", b);
    } catch (JSONException e) {
    }
    this.raiseEvent("onOfferwallAvailable", data);
  }

  @Override
  public void onOfferwallOpened() {
    this.raiseEvent("onOfferwallOpened");
  }

  @Override
  public void onOfferwallShowFailed(IronSourceError ironSourceError) {
    final JSONObject data = new JSONObject();
    try {
      data.put("errorCode", ironSourceError.getErrorCode());
      data.put("errorMessage", ironSourceError.getErrorMessage());
    } catch (JSONException e) {
    }
    this.raiseEvent("onOfferwallShowFailed", data);
  }

  @Override
  public boolean onOfferwallAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {
    final JSONObject data = new JSONObject();
    try {
      data.put("credits", credits);
      data.put("totalCredits", totalCredits);
      data.put("totalCreditsFlag", totalCreditsFlag);
    } catch (JSONException e) {
    }
    this.raiseEvent("onOfferwallAdCredited", data);
    return false;
  }

  @Override
  public void onGetOfferwallCreditsFailed(IronSourceError ironSourceError) {
    final JSONObject data = new JSONObject();
    try {
      data.put("errorCode", ironSourceError.getErrorCode());
      data.put("errorMessage", ironSourceError.getErrorMessage());
    } catch (JSONException e) {
    }
    this.raiseEvent("onGetOfferwallCreditsFailed", data);
  }

  @Override
  public void onOfferwallClosed() {
    this.raiseEvent("onOfferwallClosed");
  }

  // --------- IronSource Interstitial Listener ---------


  @Override
  public void onInterstitialAdReady() {
    this.raiseEvent("onInterstitialAdReady");
  }

  @Override
  public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
    final JSONObject data = new JSONObject();
    try {
      data.put("errorCode", ironSourceError.getErrorCode());
      data.put("errorMessage", ironSourceError.getErrorMessage());
    } catch (JSONException e) {
    }
    this.raiseEvent("onInterstitialAdLoadFailed", data);
  }

  @Override
  public void onInterstitialAdOpened() {
    this.raiseEvent("onInterstitialAdOpened");
  }

  @Override
  public void onInterstitialAdClosed() {
    this.raiseEvent("onInterstitialAdClosed");
  }

  @Override
  public void onInterstitialAdShowSucceeded() {
    this.raiseEvent("onInterstitialAdShowSucceeded");
  }

  @Override
  public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
    final JSONObject data = new JSONObject();
    try {
      data.put("errorCode", ironSourceError.getErrorCode());
      data.put("errorMessage", ironSourceError.getErrorMessage());
    } catch (JSONException e) {
    }
    this.raiseEvent("onInterstitialAdLoadFailed", data);
  }

  @Override
  public void onInterstitialAdClicked() {
    this.raiseEvent("onInterstitialAdClicked");
  }


}
