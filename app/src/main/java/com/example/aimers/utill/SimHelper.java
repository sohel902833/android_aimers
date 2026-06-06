package com.example.aimers.utill;

import android.content.Context;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects active SIM cards and returns them as SimInfo items.
 *
 * Requires permission: android.permission.READ_PHONE_STATE
 */
public class SimHelper {

    public static class SimInfo {
        public final int    subscriptionId;  // pass to SmsManager.getSmsManagerForSubscriptionId()
        public final String displayName;     // e.g. "SIM 1 (Airtel)"
        public final int    slotIndex;       // 0-based physical slot

        public SimInfo(int subscriptionId, String displayName, int slotIndex) {
            this.subscriptionId = subscriptionId;
            this.displayName    = displayName;
            this.slotIndex      = slotIndex;
        }

        @Override
        public String toString() { return displayName; }
    }

    /**
     * Returns a list of active SIMs with user-friendly names.
     * Falls back to a single "Default SIM" entry when the API
     * is unavailable or permission is missing.
     */
    public static List<SimInfo> getAvailableSims(Context ctx) {
        List<SimInfo> result = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                SubscriptionManager sm = (SubscriptionManager)
                        ctx.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

                if (sm != null) {
                    List<SubscriptionInfo> infos = sm.getActiveSubscriptionInfoList();
                    if (infos != null && !infos.isEmpty()) {
                        for (SubscriptionInfo info : infos) {
                            String carrier    = info.getCarrierName() != null
                                    ? info.getCarrierName().toString()
                                    : "Unknown";
                            int    slot       = info.getSimSlotIndex() + 1; // 1-based for display
                            String label      = "SIM " + slot + " (" + carrier + ")";
                            result.add(new SimInfo(info.getSubscriptionId(), label,
                                    info.getSimSlotIndex()));
                        }
                        return result;
                    }
                }
            } catch (SecurityException e) {
                // READ_PHONE_STATE not granted — fall through to default
            }
        }

        // Fallback: single default SIM entry
        result.add(new SimInfo(-1, "Default SIM", 0));
        return result;
    }
}