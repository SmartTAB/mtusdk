# MAGTEK MTU SDK

## Repository Overview

This repository provides versioned sets of `.so` and `.jar` files for easy distribution to client production and development systems. You can update POS systems and manage different file versions seamlessly when MagTek releases new updates. The `mtusdk-native` module includes the `SoExtractor` class, which extracts `.so` files to the root directory. Files will only be replaced if their sizes differ from the existing files. The client system must add the extraction path to the classpath.

## Project Structure

- **`mtusdk/com/magtek/mtusdk-java`**: This folder contains the Java `.jar` file provided by MagTek as part of the [MagTek Universal SDK for MMS Devices (Java) - PN 1000008300 v100](https://www.magtek.com/Content/SoftwarePackages/1000008300.zip).

- **`mtusdk/com/magtek/mtusdk-native`**: This contains the compiled `.so` files based on the [MagTek Universal SDK for MMS Devices (Linux) - PN 1000009347](https://www.magtek.com/Content/SoftwarePackages/1000009347.zip). This Maven library also includes a self-extraction API, and it can be run from the command line for testing.

- **`mtaesdukpt-native`**: This contains the compiled `.so` files based on MagTek DKUPT AES project to decrypt card holder name and expiration date.

## Usage Instructions

```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://repo1.maven.org/maven2</url>
    </repository>
    <repository>
        <id>github-mtusdk-repo</id>
        <url>https://github.com/SmartTAB/mtusdk/raw/main/</url>
    </repository>
</repositories>


<!-- Java dependency -->
<dependency>
	<groupId>com.magtek</groupId>
	<artifactId>mtusdk-java</artifactId>
	<version>preliminary-2024-09-05</version>
</dependency>

<!-- Native Linux .so files -->
<dependency>
	<groupId>com.magtek</groupId>
	<artifactId>mtusdk-native</artifactId>
	<version>preliminary-2024-09-05</version>
	<classifier>linux-x86_64</classifier>
</dependency>

<dependency>
	<groupId>com.magtek</groupId>
	<artifactId>mtaesdukpt-native</artifactId>
	<version>1000009782-101</version>
	<classifier>linux-x86_64</classifier>
</dependency>
```