package com.google.unity;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.InterstitialAd;
import com.google.ads.mediation.admob.AdMobAdapterExtras;
import com.unity3d.player.UnityPlayer;

import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class AdMobPlugin implements AdListener {
	public static final String VERSION = "1.0";
	private static final String LOGTAG = "AdMobPlugin";
	private static AdMobPlugin instance;
	private AdView adView;
	private Activity activity;
	private String callbackHandlerName;
	protected InterstitialAd interstitial;

	public static AdMobPlugin instance() {
		if (instance == null) {
			instance = new AdMobPlugin();
		}
		return instance;
	}
	
	public static void createInterstitialAd(final Activity activity,
			final String publisherId, final boolean shortTimeout){
		Log.d(LOGTAG, "called createInterstitialAd in Java code");
		final AdMobPlugin plugin = instance();
		plugin.activity = activity;
		activity.runOnUiThread(new Runnable() {
			public void run() {
				plugin.interstitial = new InterstitialAd(activity, publisherId, shortTimeout);
				plugin.interstitial.setAdListener(plugin);
			}
		});
	}
	
	public static void requestInterstitialAd(boolean isTesting) {
		requestInterstitialAd(isTesting, null);
	}
	
	public static void requestInterstitialAd(final boolean isTesting,
			final String extrasJson) {
		AdMobPlugin plugin = instance();
		if (plugin.activity == null) {
			Log.e(LOGTAG,
					"Activity is null. Call createInterstitialAd before requestInterstitialAd.");
			return;
		}

		plugin.activity.runOnUiThread(new Runnable() {
			public void run() {
				if (AdMobPlugin.instance.interstitial == null) {
					Log.e(LOGTAG,
							"Interstitial is null. Aborting requestInterstitial.");
				} else {
					AdRequest adRequest = new AdRequest();
					if (isTesting) {
						adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
					}
					AdMobAdapterExtras extras = new AdMobAdapterExtras();
					if (extrasJson != null) {
						try {
							JSONObject extrasObject = new JSONObject(extrasJson);
							Iterator<?> extrasIterator = extrasObject.keys();
							while (extrasIterator.hasNext()) {
								String key = (String) extrasIterator.next();
								extras.addExtra(key, extrasObject.get(key));
							}
						} catch (JSONException exception) {
							Log.e(LOGTAG,
									"Extras are malformed. Ignoring ad request.");
							return;
						}
					}
					extras.addExtra("unity", Integer.valueOf(1));
					adRequest.setNetworkExtras(extras);
					AdMobPlugin.instance.interstitial.loadAd(adRequest);
				}
			}
		});
	}
	
	public static void showInterstitialAd() {
		AdMobPlugin plugin = instance();
		if (plugin.activity == null) {
			Log.e(LOGTAG,
					"Activity is null. Call createInterstitialAd before showInterstitialAd.");
			return;
		}

		plugin.activity.runOnUiThread(new Runnable() {
			public void run() {
				if (AdMobPlugin.instance.interstitial == null) {
					Log.e(LOGTAG,
							"Interstitial is null. Aborting showInterstitialAd.");
					return;
				}
				AdMobPlugin.instance.interstitial.show();
			}
		});
	}
	
	public static void createBannerView(final Activity activity,
			final String publisherId, final String adSizeString,
			final boolean positionAtTop) {
		Log.d(LOGTAG, "called createBannerView in Java code");
		final AdMobPlugin plugin = instance();
		plugin.activity = activity;
		activity.runOnUiThread(new Runnable() {
			public void run() {
				AdSize adSize = AdMobPlugin.adSizeFromSize(adSizeString);
				if (adSize == null) {
					Log.e(LOGTAG,
							"AdSize is null. Did you use an AdSize constant?");
					return;
				}
				plugin.adView = new AdView(activity, adSize, publisherId);
				plugin.adView.setAdListener(plugin);
				LinearLayout layout = new LinearLayout(activity);
				FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
						-1, -2);
				layoutParams.gravity = (positionAtTop ? 48 : 80);
				activity.addContentView(layout, layoutParams);
				LinearLayout.LayoutParams adParams = new LinearLayout.LayoutParams(
						-1, -1);
				layout.addView(plugin.adView, adParams);
			}
		});
	}

	public static void setCallbackHandlerName(String callbackHandlerName) {
		AdMobPlugin plugin = instance();
		plugin.callbackHandlerName = callbackHandlerName;
	}

	public static void requestBannerAd(boolean isTesting) {
		requestBannerAd(isTesting, null);
	}

	public static void requestBannerAd(final boolean isTesting,
			final String extrasJson) {
		AdMobPlugin plugin = instance();
		if (plugin.activity == null) {
			Log.e(LOGTAG,
					"Activity is null. Call createBannerView before requestBannerAd.");
			return;
		}

		plugin.activity.runOnUiThread(new Runnable() {
			public void run() {
				if (AdMobPlugin.instance.adView == null) {
					Log.e(LOGTAG,
							"AdView is null. Aborting requestBannerAd.");
				} else {
					AdRequest adRequest = new AdRequest();
					if (isTesting) {
						adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
					}
					AdMobAdapterExtras extras = new AdMobAdapterExtras();
					if (extrasJson != null) {
						try {
							JSONObject extrasObject = new JSONObject(extrasJson);
							Iterator<?> extrasIterator = extrasObject.keys();
							while (extrasIterator.hasNext()) {
								String key = (String) extrasIterator.next();
								extras.addExtra(key, extrasObject.get(key));
							}
						} catch (JSONException exception) {
							Log.e(LOGTAG,
									"Extras are malformed. Ignoring ad request.");
							return;
						}
					}
					extras.addExtra("unity", Integer.valueOf(1));
					adRequest.setNetworkExtras(extras);
					AdMobPlugin.instance.adView.loadAd(adRequest);
				}
			}
		});
	}

	public static void showBannerView() {
		AdMobPlugin plugin = instance();
		if (plugin.activity == null) {
			Log.e(LOGTAG,
					"Activity is null. Call createBannerView before showBannerView.");
			return;
		}

		plugin.activity.runOnUiThread(new Runnable() {
			public void run() {
				if (AdMobPlugin.instance.adView == null) {
					Log.e(LOGTAG,
							"AdView is null. Aborting showBannerView.");
					return;
				}
				AdMobPlugin.instance.adView.setVisibility(0);
			}
		});
	}

	public static void hideBannerView() {
		Log.d(LOGTAG, "called hideBannerView in Java code");
		AdMobPlugin plugin = instance();
		if (plugin.activity == null) {
			Log.e(LOGTAG,
					"Activity is null. Call createBannerView before hideBannerView.");
			return;
		}

		plugin.activity.runOnUiThread(new Runnable() {
			public void run() {
				if (AdMobPlugin.instance.adView == null) {
					Log.e(LOGTAG,
							"AdView is null. Aborting hideBannerView.");
					return;
				}
				AdMobPlugin.instance.adView.setVisibility(8);
			}
		});
	}
	
	private static AdSize adSizeFromSize(String size)
	  {
	    if ("BANNER".equals(size))
	      return AdSize.BANNER;
	    if ("IAB_MRECT".equals(size))
	      return AdSize.IAB_MRECT;
	    if ("IAB_BANNER".equals(size))
	      return AdSize.IAB_BANNER;
	    if ("IAB_LEADERBOARD".equals(size))
	      return AdSize.IAB_LEADERBOARD;
	    if ("SMART_BANNER".equals(size)) {
	      return AdSize.SMART_BANNER;
	    }
	    Log.w(LOGTAG, String.format("Unexpected ad size string: %s", new Object[] { size }));
	    return null;
	  }

	public void onReceiveAd(Ad ad)
	  {
	    if (this.callbackHandlerName != null)
	      UnityPlayer.UnitySendMessage(this.callbackHandlerName, "OnReceiveAd", "");
	  }

	  public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode error)
	  {
	    if (this.callbackHandlerName != null)
	      UnityPlayer.UnitySendMessage(this.callbackHandlerName, "OnFailedToReceiveAd", error.toString());
	  }

	  public void onPresentScreen(Ad ad)
	  {
	    if (this.callbackHandlerName != null)
	      UnityPlayer.UnitySendMessage(this.callbackHandlerName, "OnPresentScreen", "");
	  }

	  public void onDismissScreen(Ad ad)
	  {
	    if (this.callbackHandlerName != null)
	      UnityPlayer.UnitySendMessage(this.callbackHandlerName, "OnDismissScreen", "");
	  }

	  public void onLeaveApplication(Ad ad)
	  {
	    if (this.callbackHandlerName != null)
	      UnityPlayer.UnitySendMessage(this.callbackHandlerName, "OnLeaveApplication", "");
	  }
}
