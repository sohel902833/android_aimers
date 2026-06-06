package com.example.aimers.Views.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aimers.Adapter.BatchSpinnerAdapter;
import com.example.aimers.Model.Batch;
import com.example.aimers.Model.Student;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.api.ApiRef;
import com.example.aimers.utill.SimHelper;
import com.example.aimers.utill.SimHelper.SimInfo;
import com.example.aimers.utill.SmsQueueManager;
import com.example.aimers.utill.SmsQueueManager.SmsTask;
import com.example.aimers.worker.SmsWorker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SmsManagerActivity extends AppCompatActivity {

    // ── Views ─────────────────────────────────────────────────────────────────
    private Toolbar  toolbar;
    private AppBar   appBar;
    private EditText messageBoxEt;
    private Button   sendButton, moreOptionButton;
    private Spinner  batchSpinner, simSpinner;
    private TextView queueStatusTv; // shows "X messages in queue" feedback

    // ── Data ──────────────────────────────────────────────────────────────────
    private final List<Batch>   batchList          = new ArrayList<>();
    private final List<Student> studentList        = new ArrayList<>();
    private final List<Student> sortedStudentList  = new ArrayList<>();
    private final List<SimInfo> simList            = new ArrayList<>();

    private BatchSpinnerAdapter batchSpinnerAdapter;
    private ArrayAdapter<SimInfo> simSpinnerAdapter;

    private Batch   selectedBatch;
    private SimInfo selectedSim;
    private boolean batchFirst = true;

    private ProgressDialog progressDialog;

    // ── Permission request codes ───────────────────────────────────────────────
    private static final int REQUEST_SMS_PERMISSION   = 123;
    private static final int REQUEST_PHONE_PERMISSION = 124;

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_manager);

        init();
        setupBatchSpinner();
        setupSimSpinner();
        requestRequiredPermissions();
        refreshQueueStatus();

        // ── Batch selection ───────────────────────────────────────────────────
        batchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                if (batchFirst) {
                    batchFirst = false;
                } else {
                    selectedBatch = batchList.get(i);
                    findStudents();
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ── SIM selection ─────────────────────────────────────────────────────
        simSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                selectedSim = simList.get(i);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ── More options ──────────────────────────────────────────────────────
        moreOptionButton.setOnClickListener(v -> {
            Intent intent = new Intent(SmsManagerActivity.this, BatchListActivity.class);
            intent.putExtra("target", "sms");
            startActivity(intent);
        });

        // ── Send button ───────────────────────────────────────────────────────
        sendButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(SmsManagerActivity.this,
                    Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(SmsManagerActivity.this)
                        .setTitle("SMS Permission Required")
                        .setMessage("SMS permission is required to send messages. "
                                + "Please enable it in App Settings.")
                        .setPositiveButton("Open Settings", (d, w) -> {
                            Intent intent = new Intent(
                                    android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(android.net.Uri.fromParts(
                                    "package", getPackageName(), null));
                            startActivity(intent);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return;
            }
            // ── end of change ─────────────────────────────────────────────

            String message = messageBoxEt.getText().toString().trim();
            if (message.isEmpty()) {
                Toast.makeText(this, "Please enter your message", Toast.LENGTH_SHORT).show();
                return;
            }
            if (sortedStudentList.isEmpty()) {
                Toast.makeText(this, "No students found for selected batch", Toast.LENGTH_SHORT).show();
                return;
            }
            confirmAndEnqueue(message);
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setTitle("Loading…");
        progressDialog.setMessage("");
        progressDialog.show();

        // Fetch batches
        ApiRef.batchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    batchList.clear();
                    batchList.add(new Batch("all", "All", "all", "B", "a"));
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Batch batch = child.getValue(Batch.class);
                        batchList.add(batch);
                    }
                    batchSpinnerAdapter.notifyDataSetChanged();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SmsManagerActivity.this, "No Batch Found.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(SmsManagerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch students
        ApiRef.studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    studentList.clear();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Student student = child.getValue(Student.class);
                        studentList.add(student);
                    }
                } else {
                    Toast.makeText(SmsManagerActivity.this, "No Student Found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(SmsManagerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh SIM list in case the user changed SIMs
        loadSimList();
        refreshQueueStatus();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────────────────────

    private void init() {
        toolbar = findViewById(R.id.appBarId);
        appBar  = new AppBar(this);
        appBar.init(toolbar, "SMS Manager");
        appBar.hideBackButton();

        messageBoxEt     = findViewById(R.id.messageboxEt);
        sendButton       = findViewById(R.id.sendSmsButton);
        moreOptionButton = findViewById(R.id.sendSmsSeparetly);
        batchSpinner     = findViewById(R.id.batchSpinnerId);
        simSpinner       = findViewById(R.id.simSpinnerId);       // NEW — add to XML
        queueStatusTv    = findViewById(R.id.queueStatusTv);     // NEW — add to XML
        progressDialog   = new ProgressDialog(this);
    }

    private void setupBatchSpinner() {
        batchSpinnerAdapter = new BatchSpinnerAdapter(this, batchList);
        batchSpinner.setAdapter(batchSpinnerAdapter);
    }

    private void setupSimSpinner() {
        loadSimList();
        simSpinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, simList);
        simSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        simSpinner.setAdapter(simSpinnerAdapter);

        if (!simList.isEmpty()) selectedSim = simList.get(0);
    }

    private void loadSimList() {
        simList.clear();
        simList.addAll(SimHelper.getAvailableSims(this));
        if (simSpinnerAdapter != null) simSpinnerAdapter.notifyDataSetChanged();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Queue management
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Shows a confirmation dialog, then builds the task list, saves it to
     * SharedPreferences, and kicks off the WorkManager chain.
     */
    private void confirmAndEnqueue(String message) {
        int count       = 0;
        int noPhone     = 0;
        for (Student s : sortedStudentList) {
            if (s.getPhone() != null && !s.getPhone().isEmpty()) count++;
            else noPhone++;
        }

        int simSubId   = selectedSim != null ? selectedSim.subscriptionId : -1;
        String simName = selectedSim != null ? selectedSim.displayName    : "Default SIM";
        String batchLbl = selectedBatch != null ? selectedBatch.getBatchName() : "All";

        String msg = "Ready to send " + count + " SMS\n"
                + "Batch : " + batchLbl + "\n"
                + "SIM   : " + simName + "\n\n"
                + (noPhone > 0 ? "⚠ " + noPhone + " students have no phone number.\n\n" : "")
                + "Messages will be sent one by one in the background (~11 s apart).\n"
                + "You can close the app — sending will continue.";

        new AlertDialog.Builder(this)
                .setTitle("Confirm Send")
                .setMessage(msg)
                .setPositiveButton("Send", (d, w) -> {
                    buildAndStartQueue(message, simSubId);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void buildAndStartQueue(String message, int simSubId) {
        List<SmsTask> tasks = new ArrayList<>();

        for (Student student : sortedStudentList) {
            String number = student.getPhone();
            if (number != null && !number.isEmpty()) {
                tasks.add(new SmsTask(number, student.getName(), message, simSubId));
            }
        }

        if (tasks.isEmpty()) {
            Toast.makeText(this, "No valid phone numbers found.", Toast.LENGTH_SHORT).show();
            return;
        }

        SmsQueueManager.enqueue(this, tasks);
        SmsWorker.startQueue(this);

        refreshQueueStatus();
        Toast.makeText(this,
                tasks.size() + " messages queued. Sending in background…",
                Toast.LENGTH_LONG).show();
    }

    /** Shows pending/total counts next to the send button. */
    private void refreshQueueStatus() {
        if (queueStatusTv == null) return;
        List<SmsTask> all     = SmsQueueManager.getAll(this);
        long pending          = 0, sent = 0, failed = 0;
        for (SmsQueueManager.SmsTask t : all) {
            switch (t.status) {
                case SmsQueueManager.STATUS_PENDING: pending++; break;
                case SmsQueueManager.STATUS_SENT:    sent++;    break;
                case SmsQueueManager.STATUS_FAILED:  failed++;  break;
            }
        }
        if (all.isEmpty()) {
            queueStatusTv.setVisibility(View.GONE);
        } else {
            queueStatusTv.setVisibility(View.VISIBLE);
            queueStatusTv.setText("Queue — Pending: " + pending
                    + "  Sent: " + sent
                    + "  Failed: " + failed);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Student filtering
    // ─────────────────────────────────────────────────────────────────────────

    private void findStudents() {
        if (selectedBatch == null) return;
        sortedStudentList.clear();
        for (Student student : studentList) {
            if ("all".equals(selectedBatch.getBatchId())
                    || student.getBatchId().equals(selectedBatch.getBatchId())) {
                sortedStudentList.add(student);
            }
        }
        Toast.makeText(this,
                sortedStudentList.size() + " students found for this batch",
                Toast.LENGTH_SHORT).show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Permissions
    // ─────────────────────────────────────────────────────────────────────────

    private void requestRequiredPermissions() {
        List<String> needed = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.SEND_SMS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!needed.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    needed.toArray(new String[0]),
                    REQUEST_SMS_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_PERMISSION) {
            boolean allGranted = true;
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) { allGranted = false; break; }
            }
            if (!allGranted) {
                Toast.makeText(this,
                        "Some permissions denied. SIM list or SMS may not work.",
                        Toast.LENGTH_LONG).show();
            } else {
                // Re-load SIMs now that READ_PHONE_STATE is granted
                loadSimList();
            }
        }
    }
}