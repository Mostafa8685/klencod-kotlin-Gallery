package com.m3rd.app

import android.content.Context

/**
 * مدير طبقات النسخة والترخيص لنسخة Gallery Pro المستقلة (Dedicated Pro Architecture).
 *
 * هذه النسخة هي النسخة الاحترافية الكاملة (Gallery Pro):
 * - كافة المزايا مفتوحة تلقائياً 100% دون الحاجة لأي مفاتيح تفعيل أو قيود زمنية.
 * - الخزنة الخاصة مشفّرة بـ AES-256 غير محدودة نهائياً.
 * - مسح الأجسام بالذكاء الاصطناعي العصبي غير محدود بالدقة الفائقة.
 * - استوديو مونتاج الفيديو الكامل والقص بدون فقدان الجودة وتصدير بأي مدة زمنية.
 * - محرك الترجمة المتزامنة (.SRT / .VTT).
 * - حزمة الذكاء الاصطناعي الكاملة (AI Remaster, Portrait Bokeh, Document Clarifier, Night Sight, Palette Extractor).
 * - فاحص الصور المكررة وتنظيف المساحة الذكي.
 */
object MangoLicenseManager {

    const val STORE_PURCHASE_URL = "https://apk3rade.mangoad.online/app/com.mangog.app"
    const val LITE_VAULT_MAX_ITEMS = Int.MAX_VALUE
    const val LITE_DAILY_AI_LIMIT = Int.MAX_VALUE

    fun isPro(ctx: Context): Boolean = true

    fun getEditionName(ctx: Context): String = "Gallery Pro (النسخة الاحترافية الكاملة)"

    fun getTierBadge(ctx: Context): String = "PRO"

    fun setProLicensed(ctx: Context, enabled: Boolean, key: String = "") {}

    fun activateWithKey(ctx: Context, enteredKey: String): Boolean = true

    fun openStorePurchasePage(ctx: Context) {}

    fun getRemainingAiEraserRuns(ctx: Context): Int = Int.MAX_VALUE

    fun consumeAiEraserRun(ctx: Context): Boolean = true

    fun canAddToVault(ctx: Context, currentVaultSize: Int, incomingCount: Int): Boolean = true
}
