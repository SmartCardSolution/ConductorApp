package com.example.proto_type_12;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class First_Activity extends AppCompatActivity {


    static TextInputLayout TIL_cardID;

    boolean validcard = false;
    Toolbar toolbar;
    double CurrentBalance;
    double tempBel;


    GetCardID cardID1;

    Ticket t1;

    String Currunt_User_email;

    String Conductor_Name, T_ID = "1";

    FirebaseAuth auth;

    Spinner sp1, sp2, sp3;
    ArrayAdapter<String> adapter1, adapter2, adapter3;
    ArrayList<String> arrayList1, arrayList2, arrayList3;
    GetandSetRout getRout, getSource, getDestination;
    Button btn_pg2_send;
    TextView PrizeShow, retry;

    Calendar c = Calendar.getInstance();

    String day, month, year, h, min, tm;

    DatabaseReference reference1, reference2, reference3, reference4, reference5;

    String RECIPENT_EMail, CARDID;

    double getsourcekm, getdestinationkm, distance = 0, pay;
    ProgressDialog progressDialog;

    Bundle bundle;

    @Override
    protected void onStart() {
        super.onStart();
        if (!isConnected()) {
            TIL_cardID.setEnabled(false);
            sp1.setEnabled(false);
            sp2.setEnabled(false);
            sp3.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Please Connect To Internet", Toast.LENGTH_SHORT).show();
            AlertDialogBox();
        } else {
            TIL_cardID.setEnabled(true);
            sp1.setEnabled(true);
            sp2.setEnabled(true);
            sp3.setEnabled(true);
            ConductorInfo();
            try {
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.setMessage("Updating Routs");
                        progressDialog.show();
                        if (snapshot.exists()) {
                            adapter1.clear();
                            for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                                getRout = datasnapshot.getValue(GetandSetRout.class);
                                arrayList1.add(String.valueOf(getRout.getRout_Number()));
                                sp1.setAdapter(adapter1);
                                progressDialog.dismiss();
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(First_Activity.this, "Please Enter Routs 1st", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } catch (Exception e) {
                Toast.makeText(First_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void ConductorInfo() {
        DatabaseReference CheckDB = FirebaseDatabase.getInstance().getReference("Conductor");

        Currunt_User_email = auth.getCurrentUser().getEmail();

        CheckDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.hasChildren()) {
                        if (Currunt_User_email.equals(ds.getValue(RegisterUser.class).getEmail())) {
                            Conductor_Name = ds.getValue(RegisterUser.class).getfName() + " " + ds.getValue(RegisterUser.class).getlName();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void mypdf(View view, String tn, String d, String s, String r, String py, String con, String dt, String tm) {


        PdfDocument pdfDocument = new PdfDocument();
        Paint p1 = new Paint();
        Paint p = new Paint();
        Paint title_p = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        p.setTextSize(30);
        p.setColor(Color.rgb(171, 70, 210));

        p1.setTextAlign(Paint.Align.LEFT);
        p1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        p1.setColor(Color.rgb(12, 30, 128));
        p1.setTextSize(40);

        title_p.setTextAlign(Paint.Align.CENTER);
        title_p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title_p.setTextSize(70);

        canvas.drawText("Bus Ticket", 1200 / 2, 80, title_p);


        String s1 = "Ticket No.:";
        page.getCanvas().drawText(s1, 50, 150, p1);
        p.setTextAlign(Paint.Align.CENTER);
        page.getCanvas().drawText(tn, 200, 180, p);

        String s2 = "Rout:";
        page.getCanvas().drawText(s2, 500, 150, p1);
        p.setTextAlign(Paint.Align.RIGHT);
        page.getCanvas().drawText(r, 500 + 100, 180, p);

        String s3 = "Source:";
        page.getCanvas().drawText(s3, 50, 230, p1);
        page.getCanvas().drawText(s, 250 + 150, 260, p);

        String s4 = "Destination:";
        page.getCanvas().drawText(s4, 500, 230, p1);
        page.getCanvas().drawText(d, 550 + 250, 260, p);

        String s5 = "Pay:";
        page.getCanvas().drawText(s5, 50, 350, p1);
        page.getCanvas().drawText(py, 200, 350, p);

        String s6 = "Conductor:";
        page.getCanvas().drawText(s6, 500, 350, p1);
        page.getCanvas().drawText(con, 550 + 250, 380, p);

        String s7 = "Date:";
        page.getCanvas().drawText(s7, 0, 50, p1);
        page.getCanvas().drawText(dt, 250, 50, p);

        String s8 = "Time:";
        page.getCanvas().drawText(s8, 850, 50, p1);
        page.getCanvas().drawText(tm, 1100, 50, p);

        pdfDocument.finishPage(page);

        String FP = Environment.getExternalStorageDirectory().getPath() + "/Download/Customer.pdf";
        File f = new File(FP);
        try {
            pdfDocument.writeTo(new FileOutputStream(f));
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        pdfDocument.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);

        bundle = savedInstanceState;

        initialize();


        btn_pg2_send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    String cmp1 = sp2.getSelectedItem().toString();
                    String cmp2 = sp3.getSelectedItem().toString();
                    if (cmp1.equals(cmp2)) {
                        showError();
                    } else {
                        DatabaseReference CardIDdb = FirebaseDatabase.getInstance().getReference("Customer");
                        CardIDdb.addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot cardidDB : dataSnapshot.getChildren()) {
                                    if (TIL_cardID.getEditText().getText().toString().equals(cardidDB.getValue(RegisterUser.class).getCardID())) {
                                        RECIPENT_EMail = cardidDB.getValue(RegisterUser.class).getEmail();
                                        CARDID = cardidDB.getValue(RegisterUser.class).getCardID();
                                        CurrentBalance = Double.valueOf(cardidDB.getValue(RegisterUser.class).getBalance());
                                        validcard = true;
                                        break;
                                    } else {
                                        validcard = false;
                                    }
                                }
                                if (!validcard) {
                                    Toast.makeText(First_Activity.this, "Invalid Card", Toast.LENGTH_SHORT).show();
                                } else {

                                    ActivityCompat.requestPermissions(First_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

                                    Calendar c = Calendar.getInstance();
                                    String d, m, y;
                                    d = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                                    m = String.valueOf(c.get(Calendar.MONTH) + 1);
                                    y = String.valueOf(c.get(Calendar.YEAR));

                                    DatabaseReference CheckDB1 = FirebaseDatabase.getInstance().getReference("Customer").child(TIL_cardID.getEditText().getText().toString().trim()).child("Tickets").child(d + "-" + m + "-" + y);
                                    if (CheckDB1 != null) {
                                        CheckDB1.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                    T_ID = String.valueOf(Integer.parseInt(ds.getValue(Ticket.class).getTicket_No().substring(7, 8)) + 1);
                                                }
                                                final androidx.appcompat.app.AlertDialog.Builder getEmail = new androidx.appcompat.app.AlertDialog.Builder(First_Activity.this);
                                                getEmail.setCancelable(false);
                                                getEmail.setTitle("Send E-Ticket");
                                                getEmail.setMessage("Send E-Ticket to " + RECIPENT_EMail);


                                                getEmail.setPositiveButton("Ok", (dialog, which) -> {

                                                    day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
                                                    month = String.valueOf(c.get(Calendar.MONTH) + 1);
                                                    year = String.valueOf(c.get(Calendar.YEAR));
                                                    h = String.valueOf(c.get(Calendar.HOUR));
                                                    min = String.valueOf(c.get(Calendar.MINUTE));
                                                    if (c.get(Calendar.HOUR_OF_DAY) > 12) {
                                                        tm = "PM";
                                                    } else {
                                                        tm = "AM";
                                                    }

                                                    DatabaseReference rf7 = FirebaseDatabase.getInstance().getReference("Customer").child(TIL_cardID.getEditText().getText().toString().trim()).child("Tickets");
                                                    t1.setDate(day + "-" + month + "-" + year);
                                                    t1.setCond(Conductor_Name);
                                                    t1.setDestination(sp3.getSelectedItem().toString());
                                                    t1.setSource(sp2.getSelectedItem().toString());
                                                    t1.setPay(String.valueOf(pay));
                                                    t1.setTime(h + ":" + min + " " + tm);
                                                    t1.setTicket_No(t1.getDate().replace("-", "") + "0" + T_ID);
                                                    t1.setRout(sp1.getSelectedItem().toString());

                                                    mypdf(v, t1.getTicket_No(), t1.getDestination(), t1.getSource(),
                                                            t1.getRout(), t1.getPay(), t1.getCond(), t1.getDate(), t1.getTime());

                                                    rf7.child(t1.getDate()).child(t1.getTicket_No()).setValue(t1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                DatabaseReference UpdataeBlance = FirebaseDatabase.getInstance().getReference("Customer").child(TIL_cardID.getEditText().getText().toString().trim());
                                                                RegisterUser bel = new RegisterUser();
                                                                if (CurrentBalance <= 25) {
                                                                    Toast.makeText(First_Activity.this, "Not enough Balance", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    tempBel = CurrentBalance - pay;
                                                                    if (tempBel <= 25) {
                                                                        bel.setBalance(String.valueOf(tempBel));
                                                                        UpdataeBlance.child("balance").setValue(bel.getBalance());
                                                                        sendMail(1);
                                                                    } else {
                                                                        bel.setBalance(String.valueOf(tempBel));
                                                                        UpdataeBlance.child("balance").setValue(bel.getBalance());
                                                                        sendMail(0);
                                                                    }
                                                                }
                                                            } else {
                                                                Toast.makeText(First_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                });
                                                getEmail.show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    AlertDialogBox();
                }
            }
        });

        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {

                adapter2.clear();
                adapter3.clear();

                String T2, T3;
                T2 = T3 = sp1.getItemAtPosition(position).toString();
                reference2 = FirebaseDatabase.getInstance().getReference("stops").child(T2);
                reference3 = FirebaseDatabase.getInstance().getReference("stops").child(T3);


                itemAddInSP2();
                itemAddInSP3();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {
                String Spinner2 = sp2.getItemAtPosition(position).toString();
                Spinner2 = Spinner2.replace(".", "_");
                Spinner2 = Spinner2.replace("/", "--");
                reference4 = FirebaseDatabase.getInstance().getReference("stops").child(sp1.getSelectedItem().toString()).child(Spinner2);
                reference4.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            getsourcekm = dataSnapshot.getValue(GetandSetRout.class).getKilometre();
                            if (payment() > 0) {
                                GETCARDID();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,
                                       long id) {
                String Spinner3 = sp3.getItemAtPosition(position).toString();
                Spinner3 = Spinner3.replace(".", "_");
                Spinner3 = Spinner3.replace("/", "--");
                reference5 = FirebaseDatabase.getInstance().getReference("stops").child(sp1.getSelectedItem().toString()).child(Spinner3);
                reference5.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            getdestinationkm = dataSnapshot.getValue(GetandSetRout.class).getKilometre();
                            if (payment() > 0) {
                                GETCARDID();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void GETCARDID() {
        DatabaseReference getcardid = FirebaseDatabase.getInstance().getReference("CardID");
        getcardid.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() & !snapshot.getValue(GetCardID.class).getValue().equalsIgnoreCase(" ")) {
                    cardID1 = snapshot.getValue(GetCardID.class);
                    TIL_cardID.getEditText().setText(cardID1.getValue());
                } else {
                    Toast.makeText(First_Activity.this, "Please Scan Card Again", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private double payment() {
        String cmp1 = sp2.getSelectedItem().toString();
        String cmp2 = sp3.getSelectedItem().toString();
        if (cmp1.equals(cmp2)) {
            pay = 0.0;
            distance = 0;
        } else {
            distance = Math.abs(getsourcekm - getdestinationkm);
            if (distance <= 5) {
                pay = 3.0;
            } else {
                pay = 5.0;
            }
        }
        PrizeShow.setText("₹ " + pay);
        return distance;
    }

    private void sendMail(int RequestCode) {
        String mail = RECIPENT_EMail;
        String message = "Dear Passenger,\n" +
                "            Your payment of ₹ " + pay + "  is debited from the card number " + CARDID + " for the city bus Route " + sp1.getSelectedItem().toString() + " from " + sp2.getSelectedItem().toString() + " to " + sp3.getSelectedItem().toString() + " is accepted by conductor " + Conductor_Name + " and your ticket is booked. And your current balance is ₹ " + tempBel + "\n" +
                "Happy Journey,\n" +
                "Bus Services.";
        String subject = "Ticket issue";

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mail, subject, message, t1.getTicket_No(), true);
        javaMailAPI.SetToastMessage("E-Ticket Send");
        javaMailAPI.execute();
        TIL_cardID.getEditText().setText("");
        if (RequestCode == 1) {
            sendAlertMail();
        }
    }

    private void sendAlertMail() {

        String mail = RECIPENT_EMail;
        String message = "Dear Passenger,\n" +
                "\t\tYou have reached to minimum balance in the card number " + CARDID + ". Kindly recharge your card from the respected bus service office. Otherwise, you will not be able to use your card for payment is city bus services\n" +
                "Thank You,\n" +
                "Bus Services.";
        String subject = "Card Balance Alert";

        JavaMailAPI javaMailAPI = new JavaMailAPI(this, mail, subject, message, t1.getTicket_No(), false);
        javaMailAPI.SetToastMessage("Alert Balance Mail Send");
        javaMailAPI.execute();
        TIL_cardID.getEditText().setText("");
    }

    private void showError() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(First_Activity.this);
        builder.setCancelable(false);
        builder.setTitle("ERROR");
        builder.setMessage("You can't Select Source and Destination Stop Same");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sp2.setFocusable(true);
            }
        });
        builder.create().show();
    }

    private boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    private void itemAddInSP2() {
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    adapter2.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        if (item.hasChildren()) {
                            getSource = item.getValue(GetandSetRout.class);
                            String Temp = getSource.getStp_Name();
                            Temp = Temp.replaceAll("_", ".");
                            Temp = Temp.replaceAll("--", "/");
                            arrayList2.add(Temp);
                        }
                    }
                    sp2.setAdapter(adapter2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void itemAddInSP3() {
        reference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    adapter3.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        if (item.hasChildren()) {
                            getDestination = item.getValue(GetandSetRout.class);
                            String Temp = getDestination.getStp_Name();
                            Temp = Temp.replace("_", ".");
                            Temp = Temp.replaceAll("--", "/");
                            arrayList3.add(Temp);
                        }
                    }
                    sp3.setAdapter(adapter3);
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initialize() {

        auth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TIL_cardID = findViewById(R.id.CARD_ID);

        progressDialog = new ProgressDialog(First_Activity.this);
        sp1 = findViewById(R.id.SP1);
        sp2 = findViewById(R.id.SP2);
        sp3 = findViewById(R.id.SP3);

        PrizeShow = findViewById(R.id.Pay_ISSUE_TICKET1);

        arrayList1 = new ArrayList<>();
        adapter1 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arrayList1);

        arrayList2 = new ArrayList<>();
        adapter2 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arrayList2);

        arrayList3 = new ArrayList<>();
        adapter3 = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arrayList3);

        btn_pg2_send = findViewById(R.id.SUBMIT_BTN);

        getRout = new GetandSetRout();
        getSource = new GetandSetRout();
        getDestination = new GetandSetRout();

        reference1 = FirebaseDatabase.getInstance().getReference("stops");

        t1 = new Ticket();

        progressDialog.setCancelable(false);
        progressDialog.setTitle("Fetching details");
        progressDialog.setMessage("Processing...");
        progressDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.log_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.log_out_menu:
                auth.signOut();
                startActivity(new Intent(this, Login.class));
                finish();
                Toast.makeText(this, "Log Out Successfully", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void AlertDialogBox() {
        Dialog dialog = new Dialog(First_Activity.this, R.style.NO_INTERNET_DIALOG);
        dialog.setContentView(R.layout.no_internet_dilog);
        dialog.setCancelable(false);
        dialog.show();
        retry = dialog.findViewById(R.id.BTN_RETRY);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                progressDialog.setCancelable(false);
                progressDialog.setTitle("Waiting For Connection..");
                progressDialog.show();
                startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!isConnected()) {
                            progressDialog.dismiss();
                            AlertDialogBox();
                        } else {
                            progressDialog.dismiss();
                            GETCARDID();
                        }
                    }
                }, 5000);
            }
        });
    }
}