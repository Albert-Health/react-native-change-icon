package com.reactnativechangeicon

import android.app.Activity
import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

import android.util.Log


class ChangeIconModule(reactContext: ReactApplicationContext, private val packageName: String) :
  ReactContextBaseJavaModule(reactContext), Application.ActivityLifecycleCallbacks {
  private var classesToKill: MutableList<String> = mutableListOf<String>()
  private var iconChanged: Boolean = false;
  private var componentClass: String = ""
  private var iconNameToEnable: String = ""


  override fun getName(): String {
    return "ChangeIcon"
  }

  @ReactMethod
  fun changeIcon(enableIcon: String, promise: Promise) {
    val activity: Activity? = currentActivity
    if (activity == null || enableIcon.isEmpty()) {
      promise.reject("Icon string is empty.")
      return
    }

    if (componentClass.isEmpty())
      componentClass = activity.componentName.className

    val activeClass = "$packageName.MainActivity$enableIcon"

    if (componentClass == activeClass) {
      promise.reject("Icon already in use.")
      return
    }

    iconNameToEnable = enableIcon;

    activity.application.registerActivityLifecycleCallbacks(this)
    promise.resolve(true)

  }

  private fun completeIconChange() {

    // enable old icon
    val manager: PackageManager = this.reactApplicationContext.packageManager;
    manager.setComponentEnabledSetting(
      ComponentName(this.reactApplicationContext, componentClass),
      PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
      PackageManager.DONT_KILL_APP
    )


    // enable new icon
    manager.setComponentEnabledSetting(
      ComponentName(this.reactApplicationContext, "albert.health.MainActivity$iconNameToEnable"),
      PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
      PackageManager.DONT_KILL_APP
    )
  }

  override fun onActivityPaused(activity: Activity) {
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
  }

  override fun onActivityStarted(activity: Activity) {
  }

  override fun onActivityResumed(activity: Activity) {
  }

  override fun onActivityStopped(activity: Activity) {
    completeIconChange();
  }

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
  }

  override fun onActivityDestroyed(activity: Activity) {
  }
}
