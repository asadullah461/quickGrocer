package com.example.quickgrocer.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quickgrocer.R;
import com.example.quickgrocer.activities.PlacedOrderActivity;
import com.example.quickgrocer.adapters.MyCartAdapter;
import com.example.quickgrocer.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyCartsFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseAuth auth;
    MyCartAdapter myCartAdapter;
    List<MyCartModel> cartModelList;
    Button buy_Now;
    ProgressBar progressBar;

    int totalBill;
    TextView overTotalAmount;

    public MyCartsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_carts, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        buy_Now = root.findViewById(R.id.buy_now);

        overTotalAmount = root.findViewById(R.id.totalPriceTv);

        progressBar=root.findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        cartModelList = new ArrayList<>();
        myCartAdapter = new MyCartAdapter(getActivity(),cartModelList);
        recyclerView.setAdapter(myCartAdapter);

        db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("AddToCart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                                String documentId = documentSnapshot.getId();

                                MyCartModel myCartModel = documentSnapshot.toObject(MyCartModel.class);

                                myCartModel.setDocumentId(documentId);
                                cartModelList.add(myCartModel);
                                myCartAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }

                            calculateTotalAmount(cartModelList);

                        }
                    }

                });

        buy_Now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               List list =  cartModelList;
                if (list.size()<1){
                    Toast.makeText(getActivity(), "Add some items in your cart first!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getContext(), PlacedOrderActivity.class);
                    intent.putExtra("itemList", (Serializable) cartModelList);
                    startActivity(intent);
                }
            }
        });




        return root;
    }

    private void calculateTotalAmount(List<MyCartModel> cartModelList) {

        double totalAmount = 0.0;
        for (MyCartModel myCartModel : cartModelList){
            totalAmount += myCartModel.getTotalPrice();
        }

        overTotalAmount.setText("Total Amount : "+totalAmount);

    }


}