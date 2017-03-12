
var exec = require('cordova/exec');

var PLUGIN_NAME = 'IronSourcePlugin';

var IronSourcePlugin = {
  init: function(appKey, userId, s, f) {
    exec(s, f, PLUGIN_NAME, 'init', [appKey, userId]);
  },
  showRewardedVideo: function(placementName) {
    exec(null, null, PLUGIN_NAME, 'showRewardedVideo', [placementName]);
  },
  getRewardedVideoPlacementInfo: function(placementName, s, f) {
    exec(s, f, PLUGIN_NAME, 'getRewardedVideoPlacementInfo', [placementName]);
  },
  isRewardedVideoPlacementCapped: function(placementName, s, f) {
    exec(s, f, PLUGIN_NAME, 'isRewardedVideoPlacementCapped', [placementName]);
  },
  setDynamicUserId: function(userId) {
    exec(null, null, PLUGIN_NAME, 'setDynamicUserId', [userId]);
  },
  loadInterstitial: function() {
    exec(null, null, PLUGIN_NAME, 'loadInterstitial', []);
  },
  isInterstitialReady: function(s, f) {
    exec(s, f, PLUGIN_NAME, 'isInterstitialReady', []);
  },
  getInterstitialPlacementInfo: function(placementName, s, f) {
    exec(s, f, PLUGIN_NAME, 'getInterstitialPlacementInfo', [placementName]);
  },
  showInterstitial: function(placementName) {
    exec(null, null, PLUGIN_NAME, 'showInterstitial', [placementName]);
  },
  showOfferwall: function(placementName) {
    exec(null, null, PLUGIN_NAME, 'showOfferwall', [placementName]);
  },
  subscribeOnNotifications: function(s) {
    exec(s, null, PLUGIN_NAME, 'subscribeOnNotifications', []);
  }
};


module.exports = IronSourcePlugin;
