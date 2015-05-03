package vershitsky.kirill.myapp.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationServices;

import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import vershitsky.kirill.myapp.AppUser;
import vershitsky.kirill.myapp.Constants;
import vershitsky.kirill.myapp.FetchAddressIntentService;
import vershitsky.kirill.myapp.MainActivity;
import vershitsky.kirill.myapp.R;
import vershitsky.kirill.myapp.SaveData.DBConnection;
import vershitsky.kirill.myapp.SaveData.VolleySingleton;
import vershitsky.kirill.myapp.UserInfFragment;


public class LoginActivity extends ActionBarActivity implements ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //Location
    protected GoogleApiClient mGoogleApiClient;
    protected Location mCurrentLocation;
    protected static final String TAG = "login activity";
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
    protected String mAddressOutput;
    private AddressResultReceiver mResultReceiver;
    protected Location mLastLocation;
    protected Address address;
    private AppUser user = new AppUser();
    private static final String APP_ID = "4802878";
    private ImageView photoView;
    private TextView textView;
    private TextView locationTextView;
    private RequestQueue mRequestQueue;
    private DBConnection dbConnection;
    private boolean locationReceived = false;
    private boolean vkUserReceived = false;

    private Toolbar toolbar;

    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Log.d("VkDemoApp", "onAcceptUserToken ");
        }

        @Override
        public void onRenewAccessToken(VKAccessToken token) {
            Log.d("VkDemoApp", "onRenewAccessToken ");
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            Log.d("VkDemoApp", "onReceiveNewToken ");
        }

        @Override
        public void onCaptchaError(VKError vkError) {
            Log.d("VkDemoApp", "onCaptchaError ");
        }

        @Override
        public void onTokenExpired(VKAccessToken vkAccessToken) {
            Log.d("VkDemoApp", "onTokenExpired ");
        }

        public void onAccessDenied(VKError vkError) {
            Log.d("VkDemoApp", "onAccessDenied: " + vkError);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        photoView = (ImageView) findViewById(R.id.photoView);
        textView = (TextView) findViewById(R.id.textView);
        locationTextView = (TextView) findViewById(R.id.location_textView);
        FragmentManager fragmentManager =  getSupportFragmentManager();
         android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        UserInfFragment userInfFragment = UserInfFragment.newInstance();
        fragmentTransaction.add(R.id.userInfoFragment, userInfFragment);
        dbConnection = new DBConnection(this);
        mResultReceiver = new AddressResultReceiver(new Handler());
        buildGoogleApiClient();

        VKUIHelper.onCreate(this);

        VKSdk.initialize(sdkListener, APP_ID);
        if (VKSdk.wakeUpSession()) {
            try {
                getUserVk();
            } catch (Exception e) {
            }
        } else {
            VKSdk.authorize(VKScope.FRIENDS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    public void getUserVk() {
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "id,first_name,last_name,sex,bdate,photo_200_orig"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONObject jsonUser = response.json.getJSONArray("response").getJSONObject(0);
                    user.setAppUser(jsonUser);
                    vkUserReceived = true;
                    drawUserInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void drawUserInfo() {
        textView.setText(user.getUserInfo());
        dbConnection.loadPhoto(user.getPhotoURL(), new DBConnection.ResponseListener() {
            @Override
            public void onResponse(Object obj) {
                Bitmap photo = (Bitmap) obj;
                photoView.setImageBitmap(photo);
                user.setUserPhoto(photo);
            }

            @Override
            public void onError(VolleyError error) {
            }
        });
    }

    public void login(View view) {
        if (locationReceived && vkUserReceived) {
            dbConnection.logIn(user);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.APP_USER_KEY, user);
            startActivity(intent);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d("LAST_KNOWN_LOCATION", mLastLocation.toString());
        startIntentService();
    }

    public void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        Log.d("LAST_KNOWN_LOCATION", mLastLocation.toString());
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Location", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    protected void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            Address address = resultData.getParcelable(Constants.ADRESS_DATA_KEY);

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
                Log.d(TAG, address.toString());
                user.setLocation(address.getCountryName(), address.getAdminArea(), address.getLocality());
                locationReceived = true;
                locationTextView.setText(user.getLocation());
            }
        }
    }
}

