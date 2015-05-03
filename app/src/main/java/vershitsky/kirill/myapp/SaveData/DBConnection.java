package vershitsky.kirill.myapp.SaveData;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import vershitsky.kirill.myapp.AppUser;
import vershitsky.kirill.myapp.Constants;

/**
 * Created by Вершицкий on 03.03.2015.
 */
public class DBConnection {
    private Context context;
    private RequestQueue mRequestQueu;
    public static final String DBUrl = "https://vkmeet.iriscouch.com/app_users/";
    public static final String TAG = "COUCH_DB_CONNECTION";
    public static final class Views{
        public static final String BY_FULL_LOCATION  = "http://vkmeet.iriscouch.com/app_users/_design/_views/_view/by_full_location_sex";
        public static final String BY_COUNTRY_LOCALITY = "http://vkmeet.iriscouch.com/app_users/_design/_views/_view/by_country_loc_sex";
        public static final String BY_COUNTRY_ADMIN = "http://vkmeet.iriscouch.com/app_users/_design/_views/_view/by_country_admin_sex";
        public static final String BY_COUNTRY = "http://vkmeet.iriscouch.com/app_users/_design/_views/_view/by_country_sex";
        public static final String TEST = "http://vkmeet.iriscouch.com/app_users/_design/_views/_view/_test";
    }
    public DBConnection(Context context) {
        this.context = context;
        mRequestQueu = VolleySingleton.getInstance(context).getRequestQueue();
    }

    public void getUser(JSONObject jsFromView, final ResponseListener listener) {
        try {
            String userId = jsFromView.getString("id");
            Log.d("URL", DBUrl + userId);
            JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.GET, DBUrl + userId, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    final AppUser user = new AppUser();
                    Log.d("DBRESPONSE", response.toString());
                    user.setFullFromJson(response);
                    loadPhoto(user.getPhotoURL(), new ResponseListener() {
                        @Override
                        public void onResponse(Object obj) {
                            user.setUserPhoto((Bitmap) obj);
                            listener.onResponse(user);
                        }
                        @Override
                        public void onError(VolleyError error) {
                            listener.onError(error);
                        }
                    });
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    listener.onError(error);
                }
            });
            mRequestQueu.add(jsReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void loadPhoto(String photoURL, final ResponseListener listener) {
        ImageRequest imgReq = new ImageRequest(photoURL, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                listener.onResponse(response);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });
        mRequestQueu.add(imgReq);
    }

    public void PUT(AppUser user) {
        JsonObjectRequest putReq = new JsonObjectRequest(Request.Method.PUT, DBUrl + user.getId(), user.getJSON(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        mRequestQueu.add(putReq);
    }

    public void PUT(String id, JSONObject jsToPut) {
        JsonObjectRequest putReq = new JsonObjectRequest(Request.Method.PUT, DBUrl + id, jsToPut, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mRequestQueu.add(putReq);
    }

    public void getJson(String URL, final ResponseListener listener) {
        JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });
        mRequestQueu.add(jsReq);
    }
    public void POST(String URL,JSONObject jsonObject, final ResponseListener listener) {
        JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error);
            }
        });
        mRequestQueu.add(jsReq);
    }
    public void logIn(final AppUser userFromVK) {
        JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.GET, DBUrl + userFromVK.getId() + "?latest=true", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                AppUser userFromDB = new AppUser();
                userFromDB.setFullFromJson(response);
                if (!userFromVK.equals(userFromDB)) {
                    try {
                        JSONObject jsToSend = userFromVK.getJSON().put("_rev", response.getString("_rev"));
                        PUT(userFromVK.getId(), jsToSend);
                        Log.d(TAG, "Update user info");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode == 404) {
                    PUT(userFromVK);
                }
            }
        });
        mRequestQueu.add(jsReq);
    }

    public void getUserFromView(String viewUrlRequest, final ResponseListener listener) {
        JsonObjectRequest jsReq = new JsonObjectRequest(Request.Method.GET, viewUrlRequest, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                        JSONArray jsUsers = response.getJSONArray("rows");
                        for (int i = 0; i < jsUsers.length(); i++) {
                            final AppUser user = new AppUser();
                            user.setFullFromJson(jsUsers.getJSONObject(i).getJSONObject("value"));
                            loadPhoto(user.getPhotoURL(), new ResponseListener() {
                                @Override
                                public void onResponse(Object obj) {
                                        user.setUserPhoto((Bitmap) obj);
                                        listener.onResponse(user);
                                }

                                @Override
                                public void onError(VolleyError error) {
                                    listener.onError(error);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
        }
    }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        mRequestQueu.add(jsReq);
    }

    public interface ResponseListener {
        public void onResponse(Object obj);
        public void onError(VolleyError error);
    }
}
