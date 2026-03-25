# core-io

A lightweight, pure Java I/O utility library.

> Designed for simplicity, reusability, and clean API.

---

## Features

- 📦 Pure Java (no Android dependency)
- ⚡ Buffered I/O by default
- 🧠 Clean and readable API design
- 🔒 Safe resource handling
- 🔧 Common file & stream utilities

---

## Installation

### Step 1: Add JitPack repository

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```

### Step 2: Add dependency

```groovy
dependencies {
    implementation 'com.github.alistair:core-io:1.0.0'
}
```

## Usage

```java
// Read text from file
String text = IOUtils.readText(new File("test.txt"));

// Read lines
List<String> lines = IOUtils.readLines(new File("test.txt"));


// Write text
IOUtils.writeText(new File("out.txt"), "Hello core-io");

// Append text
        IOUtils.

appendText(new File("out.txt"), "\nAppend line");


// Copy stream
        try(
InputStream in = new FileInputStream("a.txt");
OutputStream out = new FileOutputStream("b.txt")){

        IOUtils.

copy(in, out);
}

// File utilities
File dir = new File("logs");
FileUtils.

ensureDir(dir);

String ext = FileUtils.getExtension("demo.txt"); // txt
String size = FileUtils.readableSize(2048);      // 2.00 KB

```

## API Overview

IOUtils

- copy(InputStream, OutputStream)
- readText(File)
- readLines(File)
- writeText(File, String)
- appendText(File, String)
- toByteArray(InputStream)

FileUtils

- exists(File)
- ensureDir(File)
- ensureParentDir(File)
- deleteRecursively(File)
- getFileName(String)
- getExtension(String)
- readableSize(long)

Charsets

- UTF_8
- UTF_16
- ISO_8859_1
- US_ASCII

## Requirements

- Java 8+

## License

[MIT](https://github.com/alistair/core-io/blob/master/LICENSE)

## Contributing

Pull requests are welcome.
