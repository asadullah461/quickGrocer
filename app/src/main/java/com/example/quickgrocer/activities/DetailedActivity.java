package com.example.quickgrocer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.quickgrocer.R;
import com.example.quickgrocer.models.ViewAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DetailedActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    TextView quantity;
    int totalQuantity = 1 ;
    int totalPrice = 0 ;
    ImageView detailedImg;
    TextView price,rating,description;
    Button addToCart;
    ImageView addItem,removeItem;
    Toolbar toolbar_detailed;

    ViewAllModel viewAllModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);


        toolbar_detailed = findViewById(R.id.toolbar_detailed);
        setSupportActionBar(toolbar_detailed);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore= FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final  Object object = getIntent().getSerializableExtra("detail");
        if (object instanceof ViewAllModel){
            viewAllModel = (ViewAllModel) object;
        }

        quantity = findViewById(R.id.quantity);

        detailedImg = findViewById(R.id.detailed_img);
        addItem = findViewById(R.id.add_item);
        removeItem = findViewById(R.id.remove_item);


        price = findViewById(R.id.detailed_price_textview);
        rating = findViewById(R.id.detailed_ratingbar);
        description = findViewById(R.id.detailed_description);
        if (viewAllModel != null){
            Glide.with(getApplicationContext()).load(viewAllModel.getImg_url()).into(detailedImg);
            rating.setText(viewAllModel.getRating());
            description.setText(viewAllModel.getDescription());
            price.setText("Price :Rs "+viewAllModel.getPrice()+"/kg");

            totalPrice = viewAllModel.getPrice() * totalQuantity;

            if (viewAllModel.getType().equals("egg")){
                price.setText("Price :Rs "+viewAllModel.getPrice()+"/dozen");
                totalPrice = viewAllModel.getPrice() * totalQuantity;
            }
            if (viewAllModel.getType().equals("milk")){
                price.setText("Price :Rs "+viewAllModel.getPrice()+"/litre");
                totalPrice = viewAllModel.getPrice() * totalQuantity;
            }

        }

        addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addedToCart();
            }
        });
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuantity < 100){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                    totalPrice = viewAllModel.getPrice() * totalQuantity;
                }
            }
        });
        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuantity > 1){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                    totalPrice = viewAllModel.getPrice() * totalQuantity;

                }

            }
        });

    }
    private void addedToCart() {
        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();


        SimpleDateFormat currentDate = new SimpleDateFormat("MM , DD , yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:MM:SS");
        saveCurrentTime = currentTime.format(calForDate.getTime());


        final HashMap<String,Object> cartMap = new HashMap<>();

        cartMap.put("productName",viewAllModel.getName());
        cartMap.put("productPrice",price.getText().toString());
        cartMap.put("currentDate",saveCurrentDate);
        cartMap.put("currentTime",saveCurrentTime);
        cartMap.put("totalQuantity",quantity.getText().toString());
        cartMap.put("totalPrice",totalPrice);

        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("AddToCart").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(DetailedActivity.this, "Added To A Cart", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }
}