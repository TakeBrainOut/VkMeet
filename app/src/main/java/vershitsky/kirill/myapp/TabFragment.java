package vershitsky.kirill.myapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.samples.apps.iosched.ui.widget.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vershitsky.kirill.myapp.SaveData.DBConnection;

public class TabFragment extends Fragment {
    private AppUser appUser;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<AppUser> users = new ArrayList<AppUser>();
    private DBConnection dbConnection;
    private String searchViewUrl;
    private final int START_USERS_COUNT = 5;
    private final int NUMBER_BETWEEN_USERS = 3;
    ///RecyclerView scroll listener
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;


//    private OnFragmentInteractionListener mListener;

    public static TabFragment newInstance(AppUser user, String searchViewUrl) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.APP_USER_KEY, user);
        args.putString(Constants.SEARCH_VIEW_URL_EXTRA, searchViewUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public TabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appUser = getArguments().getParcelable(Constants.APP_USER_KEY);
            searchViewUrl = getArguments().getString(Constants.SEARCH_VIEW_URL_EXTRA);
        }
        Log.d("VIEW_URL", searchViewUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_layout, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.rec_view_users);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new MyAdapter(users);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d("ON_CLICK",users.get(position).toString());
            }
        }));
        dbConnection = new DBConnection(getActivity());
        drawRecyclerView();
        return v;
    }

    ;

    public void drawRecyclerView() {
        //  DANGER - ОПАСТНОСТЬ
        // Качество кода просто зашкаливает и вызывает рвотный рефлекс
        dbConnection.getJson(searchViewUrl, new DBConnection.ResponseListener() {
            @Override
            public void onResponse(Object obj) {
                try {
                    JSONObject jsResp = (JSONObject) obj;
                    final JSONArray jsUsers = jsResp.getJSONArray("rows");
                    int requestNumber;
                    if (jsUsers.length() > START_USERS_COUNT)
                        requestNumber = START_USERS_COUNT;
                    else requestNumber = jsUsers.length();

                    for (int i = 0; i < requestNumber; i++) {
                        dbConnection.getUser(jsUsers.getJSONObject(i), new DBConnection.ResponseListener() {
                            @Override
                            public void onResponse(Object obj) {
                                AppUser user = (AppUser) obj;
                                users.add(user);
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(VolleyError error) {

                            }
                        });
                    }
                    mRecyclerView.setOnScrollListener(new EndlessScrollRecyclListener() {
                        @Override
                        public void onLoadMore(int page, int totalItemsCount) {
                            try {
                                dbConnection.getUser(jsUsers.getJSONObject(totalItemsCount), new DBConnection.ResponseListener() {
                                    @Override
                                    public void onResponse(Object obj) {
                                        AppUser user = (AppUser) obj;
                                        users.add(user);
                                        mAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onError(VolleyError error) {

                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {

            }
        });
////                  РАБОЧИЙ ВАРИАНТ - СПИСОК ПРИЛЕТАЕТ ЦЕЛИКОМ
//                dbConnection.getUserFromView(searchViewUrl, new DBConnection.ResponseListener() {
//                    @Override
//                    public void onResponse(Object obj) {
//                        users.add((AppUser) obj);
//                        mAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void onError(VolleyError error) {
//                        Log.d("ERROR", error.toString());
//                    }
//                });
//

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

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
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

}
