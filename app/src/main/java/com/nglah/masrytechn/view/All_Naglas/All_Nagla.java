package com.nglah.masrytechn.view.All_Naglas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nglah.masrytechn.Listener;
import com.nglah.masrytechn.R;
import com.nglah.masrytechn.network.networkModel.response.driver.GetAllNaqlaResponse;
import com.nglah.masrytechn.view.Accepted_Info.Accepted_User_Info;
import com.nglah.masrytechn.view.Utils.CheckNetwork;
import com.nglah.masrytechn.view.adapter.AllNaqlahAdapter;
import com.nglah.masrytechn.viewModel.DriverViewModel;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nglah.masrytechn.model.UserModel.loggedInUser;

public class All_Nagla extends AppCompatActivity implements Listener {

    @BindView(R.id.rv_allDriver)
    RecyclerView rv_branches;
    @BindView(R.id.tv_branchFragment_message)
    TextView tv_message;
    @BindView(R.id.ib_allDriver_refresh)
    ImageButton ib_refresh;
    @BindView(R.id.pb_allDriver_loading)
    ProgressBar pb_loading;
    @BindView(R.id.view_handelLoading)
    ConstraintLayout cl_loadingView;


    @BindString(R.string.networkException)
    String newtworkException;
    @BindString(R.string.poorConnection)
    String poorConnection;
    @BindString(R.string.serverError)
    String serverError;
    @BindString(R.string.no_data_found)
    String noDataFound;


    private List<GetAllNaqlaResponse.Datum> list;

    RecyclerView.LayoutManager layoutManager;
    AllNaqlahAdapter adapter;
    DriverViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__nagla);
        ButterKnife.bind(this);
        initListener();
        initAdapter();
        getDataFromServer();
    }

    @OnClick(R.id.list)
    void backto() {
        finish();
    }


    @OnClick(R.id.list)
    void back() {
        finish();
    }

    private void getDataFromServer() {
        if (new CheckNetwork(this).getConnected()) {
            showLoading();

            viewModel.getAllNaqlaFromServer(loggedInUser.getId());

        } else {
            errorNetwork();
        }
    }

    private void initAdapter() {
        layoutManager = new LinearLayoutManager(this);
        rv_branches.setLayoutManager(layoutManager);
        adapter = new AllNaqlahAdapter(list, this, this::onClick);
        rv_branches.setAdapter(adapter);
    }

    private void showLoading() {
        rv_branches.setVisibility(View.GONE);
        cl_loadingView.setVisibility(View.VISIBLE);
        tv_message.setVisibility(View.GONE);
        ib_refresh.setVisibility(View.GONE);
        pb_loading.setVisibility(View.VISIBLE);
    }

    private void dismissLoading() {
        rv_branches.setVisibility(View.VISIBLE);
        cl_loadingView.setVisibility(View.GONE);
        tv_message.setVisibility(View.GONE);
        ib_refresh.setVisibility(View.GONE);
        pb_loading.setVisibility(View.GONE);
    }

    private void errorNetwork() {
        rv_branches.setVisibility(View.GONE);
        cl_loadingView.setVisibility(View.VISIBLE);
        tv_message.setVisibility(View.VISIBLE);
        ib_refresh.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.GONE);
        tv_message.setText(poorConnection);
    }

    private void errorServer() {
        rv_branches.setVisibility(View.GONE);
        cl_loadingView.setVisibility(View.VISIBLE);
        tv_message.setVisibility(View.VISIBLE);
        ib_refresh.setVisibility(View.VISIBLE);
        pb_loading.setVisibility(View.GONE);
        tv_message.setText(serverError);
    }

    private void noData() {
        rv_branches.setVisibility(View.GONE);
        cl_loadingView.setVisibility(View.VISIBLE);
        tv_message.setVisibility(View.VISIBLE);
        ib_refresh.setVisibility(View.GONE);
        pb_loading.setVisibility(View.GONE);
        tv_message.setText(noDataFound);
    }


    @Override
    public void onClick(int position) {

        Intent intent = new Intent(this, Accepted_User_Info.class);
        intent.putExtra("data", list.get(position));
        startActivity(intent);
    }

    private void initListener() {
        viewModel = ViewModelProviders.of(this).get(DriverViewModel.class);
        viewModel.getAllNaqla().observe(this, new Observer<GetAllNaqlaResponse>() {
            @Override
            public void onChanged(GetAllNaqlaResponse response) {
                dismissLoading();
                if (response != null && response.getStatus()) {
                    if (response.getData() != null && response.getData().size() > 0) {
                        list = response.getData();
                        initAdapter();
                    } else {
                        noData();
                    }
                } else if (response.getMessage().equals(newtworkException)) {
                    errorNetwork();
                } else {
                    errorServer();
                }


            }
        });
    }

    private void inflateDesign() {
        list.clear();
    }

    @OnClick(R.id.ib_allDriver_refresh)
    void refresh() {
        getDataFromServer();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
