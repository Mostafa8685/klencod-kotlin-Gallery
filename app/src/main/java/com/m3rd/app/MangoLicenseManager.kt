package com.m3rd.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri

/**
 * مدير طبقات النسخة والترقية (Mango Tier & License Manager).
 *
 * يوفر تحكماً شاملاً بالنسختين المستقلتين:
 * 1. Mango Lite (النسخة المجانية - com.mangog.lite):
 *    - رابط الشراء المباشر للنسخة الكاملة: https://apk3rade.mangoad.online/app/com.mangog.app
 *    - تصفح فوري 60-120fps لصور وفيديوهات وصوتيات الهاتف.
 *    - الخزنة الخاصة حتى 15 عنصراً بكلمة سر وبصمة مشفّرة بـ AES-256.
 *    - 3 عمليات يومية لمسح الأجسام بالذكاء الاصطناعي (AI Object Eraser).
 *    - أزرار الشراء والترقية المباشرة تنقل المستخدم فوراً للرابط الرسمي.
 *
 * 2. Mango Pro (النسخة المدفوعة المستقلة - com.mangog.app):
 *    - تأتي مفعلة 100% افتراضياً (Default Pro) بدون أي قيود، أو يمكن تفعيلها بكود الشراء في أي بناء.
 *    - خزنة خاصة مشفّرة غير محدودة نهائياً.
 *    - مسح أجسام بالذكاء الاصطناعي غير محدود بالدقة الفائقة.
 *    - استوديو مونتاج الفيديو الكامل والقص بدون فقدان جودة.
 *    - محرك الترجمة المتزامنة (.SRT / .VTT).
 *    - حزمة الذكاء الاصطناعي الكاملة (AI Remaster, Portrait Bokeh, Document Scanner, Night Sight).
 *    - فاحص الصور المكررة وتنظيف المساحة (AI Smart Cleaner).
 */
object MangoLicenseManager {

    const val STORE_PURCHASE_URL = "https://apk3rade.mangoad.online/app/com.mangog.app"
    private const val PREFS_NAME = "mango_license_prefs"
    private const val KEY_IS_PRO_OVERRIDE = "is_pro_licensed_override"
    private const val KEY_LICENSE_KEY = "active_license_key"
    private const val KEY_DAILY_AI_DATE = "daily_ai_date"
    private const val KEY_DAILY_AI_COUNT = "daily_ai_count"

    const val LITE_VAULT_MAX_ITEMS = 15
    const val LITE_DAILY_AI_LIMIT = 3

    private val VALID_OFFLINE_PRO_KEYS = setOf(
        "MANGO-PRO-2026-PREMIUM",
        "MANGO-FLAGSHIP-VIP-UNLIMITED",
        "MANGO-STUDIO-PRO-KEY",
        "MANGO-GOLD-ACCESS-PRO"
    )

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** فحص إذا كانت الحزمة المبنية هي Pro أو إذا تم تفعيل كود الشراء */
    fun isPro(ctx: Context): Boolean {
        // إذا كانت الحزمة المنفصلة هي com.mangog.app أو البناء Pro
        val isProPackage = ctx.packageName == "com.mangog.app"
        if (isProPackage) return true
        val p = prefs(ctx)
        return p.getBoolean(KEY_IS_PRO_OVERRIDE, false)
    }

    fun getEditionName(ctx: Context): String =
        if (isPro(ctx)) "Mango Pro (النسخة الكاملة)" else "Mango Lite (النسخة المجانية)"

    fun getTierBadge(ctx: Context): String =
        if (isPro(ctx)) "PRO" else "LITE"

    fun setProLicensed(ctx: Context, enabled: Boolean, key: String = "") {
        prefs(ctx).edit()
            .putBoolean(KEY_IS_PRO_OVERRIDE, enabled)
            .putString(KEY_LICENSE_KEY, key)
            .apply()
    }

    fun activateWithKey(ctx: Context, enteredKey: String): Boolean {
        val cleanKey = enteredKey.trim().uppercase()
        val isValid = cleanKey in VALID_OFFLINE_PRO_KEYS || (cleanKey.startsWith("MANGO-PRO-") && cleanKey.length >= 14)
        if (isValid) {
            setProLicensed(ctx, true, cleanKey)
            return true
        }
        return false
    }

    /** فتح رابط شراء وتحميل النسخة الكاملة مباشرة في المتصفح */
    fun openStorePurchasePage(ctx: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(STORE_PURCHASE_URL)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            ctx.startActivity(intent)
        } catch (_: Throwable) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(STORE_PURCHASE_URL))
                ctx.startActivity(Intent.createChooser(intent, "شراء Mango Pro").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            } catch (_: Throwable) {}
        }
    }

    fun getRemainingAiEraserRuns(ctx: Context): Int {
        if (isPro(ctx)) return Int.MAX_VALUE
        val today = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US).format(java.util.Date())
        val p = prefs(ctx)
        val savedDate = p.getString(KEY_DAILY_AI_DATE, "")
        val count = if (savedDate == today) p.getInt(KEY_DAILY_AI_COUNT, 0) else 0
        return maxOf(0, LITE_DAILY_AI_LIMIT - count)
    }

    fun consumeAiEraserRun(ctx: Context): Boolean {
        if (isPro(ctx)) return true
        val today = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US).format(java.util.Date())
        val p = prefs(ctx)
        val savedDate = p.getString(KEY_DAILY_AI_DATE, "")
        val count = if (savedDate == today) p.getInt(KEY_DAILY_AI_COUNT, 0) else 0
        if (count >= LITE_DAILY_AI_LIMIT) return false
        p.edit()
            .putString(KEY_DAILY_AI_DATE, today)
            .putInt(KEY_DAILY_AI_COUNT, count + 1)
            .apply()
        return true
    }

    fun canAddToVault(ctx: Context, currentVaultSize: Int, incomingCount: Int): Boolean {
        if (isPro(ctx)) return true
        return (currentVaultSize + incomingCount) <= LITE_VAULT_MAX_ITEMS
    }
}
