package vershitsky.kirill.myapp.vk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.api.model.VKUsersArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import vershitsky.kirill.myapp.AppUser;
import vershitsky.kirill.myapp.SaveData.DBConnection;

import static com.vk.sdk.api.VKRequest.*;

/**
 * Created by Вершицкий on 02.03.2015.
 */

public class VkConnection extends VKSdkListener {
    private final static String TAG = "MyApp";
    private static final String APP_ID = "4802878";

    private static Activity activity;

    public VkConnection(Activity activity){
        this.activity = activity;
    }

    public void onResume() {
        VKUIHelper.onResume(activity);
    }

    public void onDestroy() {
        VKUIHelper.onDestroy(activity);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKUIHelper.onActivityResult(activity, requestCode, resultCode, data);
    }

    public void onAcceptUserToken(VKAccessToken token) {
        Log.d("VkDemoApp", "onAcceptUserToken " + token);
    }

    public void onReceiveNewToken(VKAccessToken newToken) {
        Log.d("VkDemoApp", "onReceiveNewToken " + newToken);
    }

    @Override
    public void onRenewAccessToken(VKAccessToken token) {
        Log.d("VkDemoApp", "onRenewAccessToken " + token);
    }
    @Override
    public void onCaptchaError(VKError captchaError) {
        Log.d("VkDemoApp", "onCaptchaError " + captchaError);
    }
    @Override
    public void onTokenExpired(VKAccessToken expiredToken) {
        Log.d("VkDemoApp", "onTokenExpired " + expiredToken);
    }

    @Override
    public void onAccessDenied(VKError authorizationError) {
        Log.d("VkDemoApp", "onAccessDenied " + authorizationError);
    }

    public void onCreate(){
        VKSdk.initialize(this, APP_ID);
        VKUIHelper.onCreate(activity);
        if (!VKSdk.wakeUpSession()){
            VKSdk.authorize(VKScope.FRIENDS);
        }
    }

    public interface Receiver{
        public void success (Object obj);
        public void fail();
    }
}
