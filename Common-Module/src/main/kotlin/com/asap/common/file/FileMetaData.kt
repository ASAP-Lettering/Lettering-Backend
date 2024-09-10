package com.asap.common.file

data class FileMetaData(
    val name: String,
    val size: Long,
    val contentType: String,
    val inputStream: () -> ByteArray
) {
}