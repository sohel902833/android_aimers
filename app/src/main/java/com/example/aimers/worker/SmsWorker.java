package com.example.aimers.worker;

import android.content.Context;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.aimers.utill.SmsQueueManager;
import com.example.aimers.utill.SmsQueueManager.SmsTask;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Sends the next PENDING SMS from the queue, then schedules itself
 * again after DELAY_SECONDS if more pending tasks remain.
 *
 * Works even when the app is in the background or killed.
 */
public class SmsWorker extends Worker {

    private static final String TAG           = "SmsWorker";
    public  static final String WORK_TAG      = "sms_queue_worker";
    private static final long   DELAY_SECONDS = 11; // 10-12 s between sends

    public SmsWorker(@NonNull Context context,
                     @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context ctx = getApplicationContext();

        SmsTask task = SmsQueueManager.peekNext(ctx);
        if (task == null) {
            Log.d(TAG, "No pending tasks — worker done.");
            return Result.success();
        }

        Log.d(TAG, "Sending SMS to " + task.studentName + " (" + task.phoneNumber + ")");

        try {
            SmsManager smsManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
                    && task.subscriptionId != -1) {
                smsManager = SmsManager.getSmsManagerForSubscriptionId(task.subscriptionId);
            } else {
                smsManager = SmsManager.getDefault();
            }

            ArrayList<String> parts = smsManager.divideMessage(task.message);
            smsManager.sendMultipartTextMessage(
                    task.phoneNumber, null, parts, null, null);

            SmsQueueManager.updateStatus(ctx, task.id, SmsQueueManager.STATUS_SENT);
            Log.d(TAG, "SMS sent ✓ to " + task.studentName);

        } catch (Exception e) {
            Log.e(TAG, "SMS failed for " + task.studentName + ": " + e.getMessage());
            SmsQueueManager.updateStatus(ctx, task.id, SmsQueueManager.STATUS_FAILED);
            // Still schedule next task even if this one failed
        }

        // Schedule the next send if more PENDING tasks remain
        if (SmsQueueManager.hasPending(ctx)) {
            scheduleNext(ctx);
        } else {
            Log.d(TAG, "Queue exhausted — no more workers scheduled.");
        }

        return Result.success();
    }

    // ── Static helpers ────────────────────────────────────────────────────────

    /**
     * Call this once after enqueuing tasks to kick off the chain.
     * Subsequent workers re-schedule themselves automatically.
     */
    public static void startQueue(Context ctx) {
        // Cancel any previously scheduled worker first to avoid duplicates
        WorkManager.getInstance(ctx).cancelAllWorkByTag(WORK_TAG);

        scheduleNext(ctx);
    }

    private static void scheduleNext(Context ctx) {
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(SmsWorker.class)
                .addTag(WORK_TAG)
                .setInitialDelay(DELAY_SECONDS, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance(ctx).enqueue(request);
        Log.d(TAG, "Next SMS worker scheduled in " + DELAY_SECONDS + "s");
    }
}