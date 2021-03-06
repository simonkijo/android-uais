package com.uais.uais.academicMaterials;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.uais.uais.R;
import com.uais.uais.academicMaterials.common.fragment.AssignmentDataProviderFragment;
import com.uais.uais.academicMaterials.common.fragment.ExampleExpandableDataProviderFragment;
import com.uais.uais.detectinternetconnection.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AssignmentFragment extends Fragment{

    static final String FRAGMENT_TAG_DATA_PROVIDER = "Assignment_data_provider";
    static final String FRAGMENT_LIST_VIEW = "Assignment_list_view";
    public final static String STUDENT_ID = "AssignmentFragment$StId";

    private RelativeLayout mProgressView;
    // flag for Network connection status
    Boolean isNetworkPresent = false;

    public static AssignmentFragment createInstanceTwo(String st){
        Bundle b = new Bundle();
        b.putString(STUDENT_ID,st);
        AssignmentFragment ad = new AssignmentFragment();
        ad.setArguments(b);
        return ad;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // get Network status
        isNetworkPresent = ConnectionDetector.isNetworkAvailable(getContext());
        return inflater.inflate(R.layout.fragment_assignment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressView = (RelativeLayout)view.findViewById(R.id.loading_progress_assignment);

        showProgress(true);
        Data();
    }

    //Shows the progress UI and hides the login form.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    //get data run background
    private void Data(){
        if(isNetworkPresent) {
            Bundle bundle = getArguments();
            final String studentId = bundle.getString(STUDENT_ID);

            final RequestParams params = new RequestParams();
            params.put("stId",studentId);

            final AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://www.uais.co.nf/mobile/studentModules.php", params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    final Activity act = getActivity();

                    final String modules_[] = new String[response.length()];

                    for (int i = 0; i < modules_.length; i++) {
                        try {
                            JSONObject jsondata = response.getJSONObject(i);
                            modules_[i] = jsondata.getString("module");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    client.post("http://www.uais.co.nf/mobile/studentAssignment.php",params,new JsonHttpResponseHandler(){

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response_) {
                            super.onSuccess(statusCode, headers, response_);
                            if (isAdded() && act != null) {  //this isAdded() removes IllegalStateException if user interrupt background process
                                showProgress(false);

                                String sitems[][] = new String[modules_.length][response_.length()];

                                for (int i = 0; i < sitems.length; i++) {
                                    for (int j = 0; j < sitems[i].length; j++) {
                                        try {
                                            JSONObject jsondata = response_.getJSONObject(j);
                                            sitems[i][j] = jsondata.getString(modules_[i]);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                try {
                                    getFragmentManager().beginTransaction()
                                            .add(AssignmentDataProviderFragment.dataPut(modules_, sitems), FRAGMENT_TAG_DATA_PROVIDER)
                                            .replace(R.id.assignment_container, new ExpandableAssignmentFragment(), FRAGMENT_LIST_VIEW)
                                            .commit();
                                }catch(IllegalStateException ie){ie.printStackTrace();}
                            }
                        }
                    });
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    showProgress(false);
                }
            });
        }else{
            showAlertDialog(getContext(),getResources().getString(R.string.no_connection),getResources().getString(R.string.no_connection_sms),false);
        }
    }
    //alert dialog
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.mipmap.ic_check_circle_black_18dp : R.mipmap.ic_error_black_18dp);

        // Setting OK Button
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }
}
