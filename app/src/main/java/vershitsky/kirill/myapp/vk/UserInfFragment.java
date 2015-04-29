package vershitsky.kirill.myapp.vk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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

import de.hdodenhof.circleimageview.CircleImageView;
import vershitsky.kirill.myapp.AppUser;
import vershitsky.kirill.myapp.Constants;
import vershitsky.kirill.myapp.FetchAddressIntentService;
import vershitsky.kirill.myapp.R;
import vershitsky.kirill.myapp.SaveData.DBConnection;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInfFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserInfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInfFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AppUser user = new AppUser();
    private CircleImageView circleImageView;
    private TextView userName;
    private static final String APP_ID = "4802878";
    DBConnection dbConnection;
    private AddressResultReceiver mResultReceiver;
    protected String mAddressOutput;
    private boolean locationReceived = false;
    private boolean vkUserReceived = false;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    protected static final String TAG = "user_info_fragment";

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

        @Override
        public void onAccessDenied(VKError vkError) {
            Log.d("VkDemoApp", "onAccessDenied: " + vkError);
        }
    };

//    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserInfFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserInfFragment newInstance() {
        UserInfFragment fragment = new UserInfFragment();
        Bundle args = new Bundle();
//        args.putParcelable(Constants.APP_USER_KEY, user);
//        fragment.setArguments(args);
        return fragment;
    }

    public UserInfFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(Constants.APP_USER_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_inf, container, false);
        circleImageView = (CircleImageView) v.findViewById(R.id.circle_image_view);
        userName = (TextView) v.findViewById(R.id.name_textView);

        dbConnection = new DBConnection(getActivity());
        mResultReceiver = new AddressResultReceiver(new Handler());
        buildGoogleApiClient();

        VKUIHelper.onCreate(getActivity());
        VKSdk.initialize(sdkListener, APP_ID);
        if (VKSdk.wakeUpSession()) {
            try {
                getUserVk();
            } catch (Exception e) {
            }
        } else {
            VKSdk.authorize(VKScope.FRIENDS);
        }
        return v;
    }

    private void getUserVk() {
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
        userName.setText(user.getUserInfo());
        dbConnection.loadPhoto(user.getPhotoURL(), new DBConnection.ResponseListener() {
            @Override
            public void onResponse(Object obj) {
                Bitmap photo = (Bitmap) obj;
                circleImageView.setImageBitmap(photo);
                user.setUserPhoto(photo);
            }

            @Override
            public void onError(VolleyError error) {
            }
        });
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d("LAST_KNOWN_LOCATION", mLastLocation.toString());
        startIntentService();
    }
    public void startIntentService() {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        Log.d("LAST_KNOWN_LOCATION", mLastLocation.toString());
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }

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
//                locationTextView.setText(user.getLocation());
            }
        }
    }
    protected void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

}
