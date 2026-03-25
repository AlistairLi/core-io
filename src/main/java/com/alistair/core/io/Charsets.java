package com.alistair.core.io;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 字符集工具类。
 *
 * <p>统一管理常用字符集，避免业务代码中散落各种字符串形式的编码名，
 * 例如 "UTF-8"、"GBK" 等，提高可读性和可维护性。</p>
 *
 * <p>说明：</p>
 * <ul>
 *     <li>优先使用 JDK 内置 {@link StandardCharsets}</li>
 *     <li>该类仅作为常用字符集统一出口</li>
 *     <li>不允许实例化</li>
 * </ul>
 */
public final class Charsets {

    /**
     * UTF-8 字符集。
     */
    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * UTF-16 字符集。
     */
    public static final Charset UTF_16 = StandardCharsets.UTF_16;

    /**
     * ISO-8859-1 字符集。
     */
    public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

    /**
     * US-ASCII 字符集。
     */
    public static final Charset US_ASCII = StandardCharsets.US_ASCII;

    /**
     * 私有构造函数，禁止实例化。
     */
    private Charsets() {
        throw new UnsupportedOperationException("No instances.");
    }
}
