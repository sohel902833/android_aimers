package com.example.aimers.utill;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages a persistent SMS queue in SharedPreferences.
 *
 * Each SmsTask holds:
 *   - id           : unique task ID
 *   - phoneNumber  : recipient
 *   - studentName  : for toast/notification display
 *   - message      : SMS body
 *   - subscriptionId: SIM subscription id (-1 = default SIM)
 *   - status       : PENDING | SENT | FAILED
 */
public class SmsQueueManager {

    private static final String PREFS_NAME  = "sms_queue_prefs";
    private static final String KEY_QUEUE   = "sms_queue";

    // ── Status constants ──────────────────────────────────────────────────────
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SENT    = "SENT";
    public static final String STATUS_FAILED  = "FAILED";

    // ── Data model ────────────────────────────────────────────────────────────
    public static class SmsTask {
        public String  id;
        public String  phoneNumber;
        public String  studentName;
        public String  message;
        public int     subscriptionId; // -1 = system default SIM
        public String  status;
        public long    createdAt;

        public SmsTask() {}

        public SmsTask(String phoneNumber, String studentName,
                       String message, int subscriptionId) {
            this.id             = UUID.randomUUID().toString();
            this.phoneNumber    = phoneNumber;
            this.studentName    = studentName;
            this.message        = message;
            this.subscriptionId = subscriptionId;
            this.status         = STATUS_PENDING;
            this.createdAt      = System.currentTimeMillis();
        }
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Append a list of tasks to the persistent queue. */
    public static void enqueue(Context ctx, List<SmsTask> tasks) {
        List<SmsTask> current = getAll(ctx);
        current.addAll(tasks);
        save(ctx, current);
    }

    /** Return the first PENDING task, or null if none. */
    public static SmsTask peekNext(Context ctx) {
        for (SmsTask task : getAll(ctx)) {
            if (STATUS_PENDING.equals(task.status)) return task;
        }
        return null;
    }

    /** Mark a task by id with the given status. */
    public static void updateStatus(Context ctx, String taskId, String status) {
        List<SmsTask> all = getAll(ctx);
        for (SmsTask task : all) {
            if (taskId.equals(task.id)) {
                task.status = status;
                break;
            }
        }
        save(ctx, all);
    }

    /** True when at least one PENDING task remains. */
    public static boolean hasPending(Context ctx) {
        for (SmsTask task : getAll(ctx)) {
            if (STATUS_PENDING.equals(task.status)) return true;
        }
        return false;
    }

    /** All tasks (any status). */
    public static List<SmsTask> getAll(Context ctx) {
        SharedPreferences prefs = prefs(ctx);
        String json = prefs.getString(KEY_QUEUE, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<SmsTask>>() {}.getType();
        List<SmsTask> list = new Gson().fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    /** Remove all tasks from the queue. */
    public static void clearAll(Context ctx) {
        prefs(ctx).edit().remove(KEY_QUEUE).apply();
    }

    /** Remove only SENT and FAILED tasks (keep PENDING). */
    public static void clearCompleted(Context ctx) {
        List<SmsTask> pending = new ArrayList<>();
        for (SmsTask task : getAll(ctx)) {
            if (STATUS_PENDING.equals(task.status)) pending.add(task);
        }
        save(ctx, pending);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private static void save(Context ctx, List<SmsTask> tasks) {
        String json = new Gson().toJson(tasks);
        prefs(ctx).edit().putString(KEY_QUEUE, json).apply();
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}