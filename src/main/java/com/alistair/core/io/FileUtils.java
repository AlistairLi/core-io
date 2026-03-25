package com.alistair.core.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 文件工具类。
 *
 * <p>提供常用的文件与目录操作，包括：</p>
 * <ul>
 *     <li>存在性判断</li>
 *     <li>目录创建</li>
 *     <li>父目录创建</li>
 *     <li>递归删除</li>
 *     <li>文件名与扩展名提取</li>
 *     <li>可读文件大小格式化</li>
 * </ul>
 *
 * <p>该类仅处理文件系统层面的能力，不处理具体 I/O 流读写逻辑，
 * 流读写相关方法请使用 {@link IOUtils}。</p>
 */
public final class FileUtils {

    /**
     * 私有构造函数，禁止实例化。
     */
    private FileUtils() {
        throw new UnsupportedOperationException("No instances.");
    }

    /**
     * 判断文件对象是否存在。
     *
     * @param file 文件对象，可以为 null
     * @return true 表示存在，false 表示不存在或 file 为 null
     */
    public static boolean exists(File file) {
        return file != null && file.exists();
    }

    /**
     * 判断是否为普通文件。
     *
     * @param file 文件对象，可以为 null
     * @return true 表示是普通文件
     */
    public static boolean isFile(File file) {
        return file != null && file.isFile();
    }

    /**
     * 判断是否为目录。
     *
     * @param file 文件对象，可以为 null
     * @return true 表示是目录
     */
    public static boolean isDirectory(File file) {
        return file != null && file.isDirectory();
    }

    /**
     * 确保目录存在。
     *
     * <p>若目录不存在则自动创建；若目标路径存在但不是目录，则抛出异常。</p>
     *
     * @param dir 目录对象，不能为空
     * @throws IOException 当创建失败或路径类型不匹配时抛出
     */
    public static void ensureDir(File dir) throws IOException {
        requireNonNull(dir, "Directory == null");

        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new IOException("Path exists but is not a directory: " + dir.getAbsolutePath());
            }
            return;
        }

        if (!dir.mkdirs() && !dir.isDirectory()) {
            throw new IOException("Failed to create directory: " + dir.getAbsolutePath());
        }
    }

    /**
     * 确保目标文件的父目录存在。
     *
     * <p>若父目录不存在则自动创建；若 file 没有父目录，则直接返回。</p>
     *
     * @param file 文件对象，不能为空
     * @throws IOException 当创建父目录失败时抛出
     */
    public static void ensureParentDir(File file) throws IOException {
        requireNonNull(file, "File == null");

        File parent = file.getParentFile();
        if (parent != null) {
            ensureDir(parent);
        }
    }

    /**
     * 创建文件（若不存在）。
     *
     * <p>若文件已存在则直接返回 true；若父目录不存在则自动创建。</p>
     *
     * @param file 文件对象，不能为空
     * @return true 表示文件存在或创建成功；false 表示创建失败
     * @throws IOException 当创建过程中发生 I/O 错误时抛出
     */
    public static boolean createFileIfAbsent(File file) throws IOException {
        requireNonNull(file, "File == null");

        if (file.exists()) {
            return file.isFile();
        }

        ensureParentDir(file);
        return file.createNewFile();
    }

    /**
     * 递归删除文件或目录。
     *
     * <p>当参数为目录时，会先递归删除其所有子文件和子目录，再删除目录本身。</p>
     *
     * @param file 目标文件或目录，可以为 null
     * @return true 表示删除成功或目标不存在；false 表示删除失败
     */
    public static boolean deleteRecursively(File file) {
        if (file == null || !file.exists()) {
            return true;
        }

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!deleteRecursively(child)) {
                        return false;
                    }
                }
            }
        }

        return file.delete();
    }

    /**
     * 获取文件大小。
     *
     * @param file 文件对象，可以为 null
     * @return 文件长度；若 file 为 null、不存在或不是普通文件，则返回 0
     */
    public static long length(File file) {
        return isFile(file) ? file.length() : 0L;
    }

    /**
     * 获取文件名。
     *
     * <p>例如：</p>
     * <ul>
     *     <li>/a/b/test.txt -> test.txt</li>
     *     <li>test.txt -> test.txt</li>
     * </ul>
     *
     * @param path 路径字符串，可以为 null
     * @return 文件名；若 path 为空则返回空字符串
     */
    public static String getFileName(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        int index1 = path.lastIndexOf('/');
        int index2 = path.lastIndexOf('\\');
        int index = Math.max(index1, index2);

        return index >= 0 ? path.substring(index + 1) : path;
    }

    /**
     * 获取文件扩展名。
     *
     * <p>例如：</p>
     * <ul>
     *     <li>test.txt -> txt</li>
     *     <li>archive.tar.gz -> gz</li>
     *     <li>README -> ""</li>
     * </ul>
     *
     * @param fileName 文件名，可以为 null
     * @return 扩展名（不包含点）；若不存在则返回空字符串
     */
    public static String getExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }

        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(lastDot + 1);
    }

    /**
     * 将字节长度格式化为可读文本。
     *
     * <p>例如：</p>
     * <ul>
     *     <li>500 -> 500 B</li>
     *     <li>1536 -> 1.50 KB</li>
     *     <li>1048576 -> 1.00 MB</li>
     * </ul>
     *
     * @param bytes 字节长度，必须 >= 0
     * @return 可读文本
     */
    public static String readableSize(long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes must be >= 0");
        }

        if (bytes < 1024) {
            return bytes + " B";
        }

        double value = bytes;
        String[] units = {"KB", "MB", "GB", "TB"};
        int unitIndex = -1;

        do {
            value = value / 1024.0;
            unitIndex++;
        } while (value >= 1024 && unitIndex < units.length - 1);

        return String.format(java.util.Locale.US, "%.2f %s", value, units[unitIndex]);
    }

    /**
     * 安全列出目录下的直接子文件。
     *
     * <p>若目录为空、目录不存在、不是目录或读取失败，则返回空列表。</p>
     *
     * @param dir 目录对象，可以为 null
     * @return 子文件列表，不为 null
     */
    public static List<File> listFilesSafely(File dir) {
        if (!isDirectory(dir)) {
            return Collections.emptyList();
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }

        List<File> result = new ArrayList<>(files.length);
        Collections.addAll(result, files);
        return result;
    }

    /**
     * 非空校验。
     *
     * @param obj     目标对象
     * @param message 异常信息
     */
    private static void requireNonNull(Object obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
    }
}
