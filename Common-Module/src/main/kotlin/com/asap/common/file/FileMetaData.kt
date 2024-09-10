package com.asap.common.file

import java.io.InputStream

data class FileMetaData(
    val name: String,
    val size: Long,
    val contentType: String,
    val inputStream: InputStream
) {
}