package com.alistair.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * I/O 工具类。
 *
 * <p>提供常用的流读写、文本读取、字节数组转换、流复制、关闭资源等能力。</p>
 *
 * <p>设计约定：</p>
 * <ul>
 *     <li>默认字符集为 UTF-8</li>
 *     <li>默认缓冲区大小为 8KB</li>
 *     <li>大部分方法不主动关闭调用方传入的流，除非方法文档明确说明</li>
 *     <li>面向基础库场景，异常以 {@link IOException} 向上抛出</li>
 * </ul>
 */
public final class IOUtils {

    /**
     * 默认缓冲区大小：8KB。
     */
    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    /**
     * 私有构造函数，禁止实例化。
     */
    private IOUtils() {
        throw new UnsupportedOperationException("No instances.");
    }

    /**
     * 将输入流中的所有数据复制到输出流中。
     *
     * <p>该方法不会关闭输入流和输出流，调用方需自行管理资源。</p>
     *
     * @param in  输入流，不能为空
     * @param out 输出流，不能为空
     * @return 实际复制的字节数
     * @throws IOException 当读取或写入失败时抛出
     */
    public static long copy(InputStream in, OutputStream out) throws IOException {
        return copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 将输入流中的所有数据复制到输出流中，并指定缓冲区大小。
     *
     * <p>该方法不会关闭输入流和输出流，调用方需自行管理资源。</p>
     *
     * @param in         输入流，不能为空
     * @param out        输出流，不能为空
     * @param bufferSize 缓冲区大小，必须大于 0
     * @return 实际复制的字节数
     * @throws IOException 当读取或写入失败时抛出
     */
    public static long copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
        requireNonNull(in, "InputStream == null");
        requireNonNull(out, "OutputStream == null");

        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be > 0");
        }

        byte[] buffer = new byte[bufferSize];
        long total = 0;
        int len;

        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
            total += len;
        }

        return total;
    }

    /**
     * 将输入流中的所有数据读取为字节数组。
     *
     * <p>该方法不会关闭输入流。</p>
     *
     * @param in 输入流，不能为空
     * @return 读取后的字节数组
     * @throws IOException 当读取失败时抛出
     */
    public static byte[] toByteArray(InputStream in) throws IOException {
        requireNonNull(in, "InputStream == null");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }

    /**
     * 读取输入流中的文本内容，默认使用 UTF-8。
     *
     * <p>该方法不会关闭输入流。</p>
     *
     * @param in 输入流，不能为空
     * @return 文本内容
     * @throws IOException 当读取失败时抛出
     */
    public static String readText(InputStream in) throws IOException {
        return readText(in, Charsets.UTF_8);
    }

    /**
     * 读取输入流中的文本内容。
     *
     * <p>该方法不会关闭输入流。</p>
     *
     * @param in      输入流，不能为空
     * @param charset 字符集，不能为空
     * @return 文本内容
     * @throws IOException 当读取失败时抛出
     */
    public static String readText(InputStream in, Charset charset) throws IOException {
        requireNonNull(in, "InputStream == null");
        requireNonNull(charset, "Charset == null");

        return readText(new InputStreamReader(in, charset));
    }

    /**
     * 读取 Reader 中的全部文本。
     *
     * <p>该方法不会关闭 Reader。</p>
     *
     * @param reader 字符输入流，不能为空
     * @return 文本内容
     * @throws IOException 当读取失败时抛出
     */
    public static String readText(Reader reader) throws IOException {
        requireNonNull(reader, "Reader == null");

        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        int len;

        while ((len = reader.read(buffer)) != -1) {
            builder.append(buffer, 0, len);
        }

        return builder.toString();
    }

    /**
     * 读取文件中的全部文本，默认使用 UTF-8。
     *
     * @param file 文件对象，不能为空
     * @return 文本内容
     * @throws IOException 当读取失败时抛出
     */
    public static String readText(File file) throws IOException {
        return readText(file, Charsets.UTF_8);
    }

    /**
     * 读取文件中的全部文本。
     *
     * @param file    文件对象，不能为空
     * @param charset 字符集，不能为空
     * @return 文本内容
     * @throws IOException 当读取失败时抛出
     */
    public static String readText(File file, Charset charset) throws IOException {
        requireNonNull(file, "File == null");
        requireNonNull(charset, "Charset == null");

        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            return readText(in, charset);
        }
    }

    /**
     * 按行读取输入流中的文本，默认使用 UTF-8。
     *
     * <p>该方法不会关闭输入流。</p>
     *
     * @param in 输入流，不能为空
     * @return 行列表，不包含换行符
     * @throws IOException 当读取失败时抛出
     */
    public static List<String> readLines(InputStream in) throws IOException {
        return readLines(in, Charsets.UTF_8);
    }

    /**
     * 按行读取输入流中的文本。
     *
     * <p>该方法不会关闭输入流。</p>
     *
     * @param in      输入流，不能为空
     * @param charset 字符集，不能为空
     * @return 行列表，不包含换行符
     * @throws IOException 当读取失败时抛出
     */
    public static List<String> readLines(InputStream in, Charset charset) throws IOException {
        requireNonNull(in, "InputStream == null");
        requireNonNull(charset, "Charset == null");

        return readLines(new InputStreamReader(in, charset));
    }

    /**
     * 按行读取 Reader 中的文本。
     *
     * <p>该方法不会关闭 Reader。</p>
     *
     * @param reader 字符输入流，不能为空
     * @return 行列表，不包含换行符
     * @throws IOException 当读取失败时抛出
     */
    public static List<String> readLines(Reader reader) throws IOException {
        requireNonNull(reader, "Reader == null");

        BufferedReader bufferedReader = (reader instanceof BufferedReader)
                ? (BufferedReader) reader
                : new BufferedReader(reader);

        List<String> lines = new ArrayList<>();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }

        return lines;
    }

    /**
     * 按行读取文件中的文本，默认使用 UTF-8。
     *
     * @param file 文件对象，不能为空
     * @return 行列表，不包含换行符
     * @throws IOException 当读取失败时抛出
     */
    public static List<String> readLines(File file) throws IOException {
        return readLines(file, Charsets.UTF_8);
    }

    /**
     * 按行读取文件中的文本。
     *
     * @param file    文件对象，不能为空
     * @param charset 字符集，不能为空
     * @return 行列表，不包含换行符
     * @throws IOException 当读取失败时抛出
     */
    public static List<String> readLines(File file, Charset charset) throws IOException {
        requireNonNull(file, "File == null");
        requireNonNull(charset, "Charset == null");

        try (Reader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), charset))) {
            return readLines(reader);
        }
    }

    /**
     * 将文本写入文件，默认使用 UTF-8，并覆盖原文件内容。
     *
     * @param file    目标文件，不能为空
     * @param content 文本内容，null 时按空字符串处理
     * @throws IOException 当写入失败时抛出
     */
    public static void writeText(File file, String content) throws IOException {
        writeText(file, content, Charsets.UTF_8, false);
    }

    /**
     * 将文本写入文件，默认使用 UTF-8。
     *
     * @param file    目标文件，不能为空
     * @param content 文本内容，null 时按空字符串处理
     * @param append  是否追加写入
     * @throws IOException 当写入失败时抛出
     */
    public static void writeText(File file, String content, boolean append) throws IOException {
        writeText(file, content, Charsets.UTF_8, append);
    }

    /**
     * 将文本写入文件。
     *
     * <p>若目标文件的父目录不存在，会自动创建。</p>
     *
     * @param file    目标文件，不能为空
     * @param content 文本内容，null 时按空字符串处理
     * @param charset 字符集，不能为空
     * @param append  是否追加写入
     * @throws IOException 当写入失败时抛出
     */
    public static void writeText(File file, String content, Charset charset, boolean append) throws IOException {
        requireNonNull(file, "File == null");
        requireNonNull(charset, "Charset == null");

        FileUtils.ensureParentDir(file);

        try (Writer writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, append), charset))) {
            writer.write(content == null ? "" : content);
            writer.flush();
        }
    }

    /**
     * 向文件末尾追加文本，默认使用 UTF-8。
     *
     * @param file    目标文件，不能为空
     * @param content 文本内容，null 时按空字符串处理
     * @throws IOException 当写入失败时抛出
     */
    public static void appendText(File file, String content) throws IOException {
        writeText(file, content, Charsets.UTF_8, true);
    }

    /**
     * 将输入流内容写入目标文件，并覆盖原文件内容。
     *
     * <p>若目标文件的父目录不存在，会自动创建。</p>
     *
     * @param in   输入流，不能为空
     * @param file 目标文件，不能为空
     * @return 实际写入的字节数
     * @throws IOException 当写入失败时抛出
     */
    public static long writeToFile(InputStream in, File file) throws IOException {
        return writeToFile(in, file, false);
    }

    /**
     * 将输入流内容写入目标文件。
     *
     * <p>若目标文件的父目录不存在，会自动创建。</p>
     *
     * @param in     输入流，不能为空
     * @param file   目标文件，不能为空
     * @param append 是否追加写入
     * @return 实际写入的字节数
     * @throws IOException 当写入失败时抛出
     */
    public static long writeToFile(InputStream in, File file, boolean append) throws IOException {
        requireNonNull(in, "InputStream == null");
        requireNonNull(file, "File == null");

        FileUtils.ensureParentDir(file);

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file, append))) {
            long total = copy(in, out);
            out.flush();
            return total;
        }
    }

    /**
     * 将输入流包装为带缓冲的输入流。
     *
     * @param in 输入流，不能为空
     * @return 带缓冲的输入流；如果本身已经是 BufferedInputStream，则直接返回原对象
     */
    public static BufferedInputStream buffer(InputStream in) {
        requireNonNull(in, "InputStream == null");
        return (in instanceof BufferedInputStream)
                ? (BufferedInputStream) in
                : new BufferedInputStream(in);
    }

    /**
     * 将输出流包装为带缓冲的输出流。
     *
     * @param out 输出流，不能为空
     * @return 带缓冲的输出流；如果本身已经是 BufferedOutputStream，则直接返回原对象
     */
    public static BufferedOutputStream buffer(OutputStream out) {
        requireNonNull(out, "OutputStream == null");
        return (out instanceof BufferedOutputStream)
                ? (BufferedOutputStream) out
                : new BufferedOutputStream(out);
    }

    /**
     * 安静地关闭资源。
     *
     * <p>关闭失败时忽略异常，适用于 finally 或兜底清理场景。</p>
     *
     * @param closeable 可关闭对象，可以为 null
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException ignored) {
            // ignore
        }
    }

    /**
     * 安静地刷新资源。
     *
     * <p>刷新失败时忽略异常，适用于兜底场景。</p>
     *
     * @param flushable 可刷新的对象，可以为 null
     */
    public static void flushQuietly(Flushable flushable) {
        if (flushable == null) {
            return;
        }

        try {
            flushable.flush();
        } catch (IOException ignored) {
            // ignore
        }
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