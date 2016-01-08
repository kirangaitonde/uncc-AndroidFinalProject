/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.example.kgaitonde.finalproject;

import android.app.Application;


import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;


public class StarterApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    // Add your initialization code here
    Parse.initialize(this, "KVewVKknOWqgSzF5CPb7dpoVhC34Hut43gbR4Zhr", "Kf3E1fMsB3Wss9moRBJUIHKUTBCzAJtQ6HjJA2lP");
    ParseInstallation.getCurrentInstallation().saveInBackground();
    ParseFacebookUtils.initialize(getApplicationContext());
    FacebookSdk.sdkInitialize(getApplicationContext());

    //ParseFacebookUtils.initialize(this);
    //FacebookSdk.sdkInitialize(getApplicationContext());
  }

}
